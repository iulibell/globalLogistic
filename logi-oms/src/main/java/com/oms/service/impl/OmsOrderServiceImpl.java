package com.oms.service.impl;

import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.core.bean.BeanUtil;
import cn.ipokerface.snowflake.SnowflakeIdGenerator;
import io.seata.spring.annotation.GlobalTransactional;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.constant.RabbitConstant;
import com.constant.RedisConstant;
import com.api.CommonResult;
import com.oms.dao.OmsOrderDao;
import com.oms.dao.OmsOrderItemDao;
import com.oms.dto.OmsOrderDto;
import com.oms.dto.TmsTransportOrderDto;
import com.oms.entity.OmsOrder;
import com.oms.entity.OmsOrderItem;
import com.oms.service.OmsOrderService;
import com.oms.service.client.TmsServiceClient;
import com.oms.service.client.WmsServiceClient;
import com.service.RedisService;
import jakarta.annotation.Resource;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Service
public class OmsOrderServiceImpl implements OmsOrderService {
    @Resource
    private OmsOrderDao omsOrderDao;
    @Resource
    private OmsOrderItemDao omsOrderItemDao;
    @Resource
    private RedisService redisService;
    @Resource
    private TmsServiceClient tmsServiceClient;
    @Resource
    private WmsServiceClient wmsServiceClient;
    @Resource
    private RabbitTemplate rabbitTemplate;
    @Resource
    private SnowflakeIdGenerator snowflakeIdGenerator;
    @Resource
    private RedisTemplate<String, Object> redisTemplate;

    @Override
    @Transactional
    @GlobalTransactional(name = "oms-add-order", rollbackFor = Exception.class)
    public void addOrder(OmsOrderDto omsOrderDto) {
        OmsOrder omsOrder = new OmsOrder();
        BeanUtil.copyProperties(omsOrderDto,omsOrder);
        omsOrder.setStatus((short) 3);
        omsOrderDao.insert(omsOrder);
        OmsOrderItem omsOrderItem = new OmsOrderItem();
        omsOrderItem.setOrderId(omsOrderDto.getOrderId());
        omsOrderItem.setSkuName(omsOrderDto.getSkuName());
        omsOrderItem.setSkuCode(omsOrderDto.getSkuCode());
        omsOrderItem.setPrice(omsOrderDto.getPrice());
        Integer quantity = omsOrderDto.getQuantity() == null ? 0 : omsOrderDto.getQuantity();
        omsOrderItem.setQuantity(quantity);
        BigDecimal unitPrice = omsOrderDto.getPrice() == null ? BigDecimal.ZERO : omsOrderDto.getPrice();
        omsOrderItem.setTotalPrice(unitPrice.multiply(BigDecimal.valueOf(quantity)));
        omsOrderItemDao.insert(omsOrderItem);

        wmsServiceClient.stockLock(omsOrderDto);

        redisService.set(RedisConstant.ORDER_KEY_PREFIX + omsOrderDto.getOrderId(),
                "",
                RedisConstant.ORDER_EXPIRE_TIME,
                TimeUnit.MINUTES);
        rabbitTemplate.convertAndSend(RabbitConstant.ORDER_TTL_EXCHANGE,
                RabbitConstant.ORDER_TTL_ROUTING_KEY,
                omsOrderDto.getOrderId());
    }

    @Override
    public List<OmsOrderDto> getOrder(int pageNum, int pageSize) {
        StpUtil.checkPermission("manager");
        StpUtil.checkLogin();
        IPage<OmsOrder> page = new Page<>(pageNum,pageSize);
        omsOrderDao.selectPage(page,null);
        return page.convert(omsOrder -> {
            OmsOrderDto omsOrderDto = new OmsOrderDto();
            BeanUtil.copyProperties(omsOrder,omsOrderDto);
            return omsOrderDto;
        }).getRecords();
    }

    @Override
    public List<OmsOrderDto> getOrderByUserForSys(String userId, int pageNum, int pageSize) {
        IPage<OmsOrder> page = new Page<>(pageNum, pageSize);
        omsOrderDao.selectPage(page, new LambdaQueryWrapper<OmsOrder>()
                .eq(OmsOrder::getUserId, userId)
                .orderByDesc(OmsOrder::getCreateTime));
        return page.convert(omsOrder -> {
            OmsOrderDto omsOrderDto = new OmsOrderDto();
            BeanUtil.copyProperties(omsOrder, omsOrderDto);
            omsOrderDto.setTransportOrderId(queryTransportOrderIdByOrderId(omsOrderDto.getOrderId()));
            return omsOrderDto;
        }).getRecords();
    }

    @Override
    public OmsOrderDto getOrderById(String orderId) {
        StpUtil.checkPermission("manager");
        StpUtil.checkLogin();
        return getOrderByIdForSys(orderId);
    }

    @Override
    public OmsOrderDto getOrderByIdForSys(String orderId) {
        OmsOrder omsOrder = omsOrderDao.selectOne(new LambdaQueryWrapper<OmsOrder>()
                .eq(OmsOrder::getOrderId,orderId));
        OmsOrderDto omsOrderDto = new OmsOrderDto();
        BeanUtil.copyProperties(omsOrder,omsOrderDto);
        omsOrderDto.setTransportOrderId(queryTransportOrderIdByOrderId(orderId));
        OmsOrderItem item = omsOrderItemDao.selectOne(new LambdaQueryWrapper<OmsOrderItem>()
                .eq(OmsOrderItem::getOrderId, orderId)
                .last("limit 1"));
        if (item != null && item.getQuantity() != null) {
            omsOrderDto.setQuantity(item.getQuantity());
        }
        return omsOrderDto;
    }

    @Override
    public int sumItemQuantityForUserGoodsBetween(String userId, String goodsId, Date startTime, Date endTime) {
        if (userId == null || userId.isBlank() || goodsId == null || goodsId.isBlank()
                || startTime == null || endTime == null) {
            return 0;
        }
        Integer n = omsOrderDao.sumItemQuantityForUserGoodsBetween(userId.trim(), goodsId.trim(), startTime, endTime);
        return n == null ? 0 : n;
    }

    @Override
    public void deleteOrder(String orderId) {
        omsOrderDao.delete(new LambdaQueryWrapper<OmsOrder>()
                .eq(OmsOrder::getOrderId,orderId));

        wmsServiceClient.stockUnlock(orderId);
    }

    @Override
    public void cancelOrder(String orderId) {
        redisService.delete(RedisConstant.ORDER_KEY_PREFIX + orderId);
        omsOrderDao.delete(new LambdaQueryWrapper<OmsOrder>()
                .eq(OmsOrder::getOrderId,orderId));

        wmsServiceClient.stockUnlock(orderId);
    }

    @Override
    @Transactional
    @GlobalTransactional(name = "oms-pay-order", rollbackFor = Exception.class)
    public boolean payForOrder(String orderId) {
        int paid = omsOrderDao.update(new LambdaUpdateWrapper<OmsOrder>()
                .eq(OmsOrder::getOrderId,orderId)
                .eq(OmsOrder::getStatus,(short)3)
                .set(OmsOrder::getStatus,(short)5));
        if (paid == 0) {
            return false;
        }

        OmsOrder omsOrder = omsOrderDao.selectOne(new LambdaQueryWrapper<OmsOrder>()
                .eq(OmsOrder::getOrderId,orderId));
        if (omsOrder == null) {
            return false;
        }
        TmsTransportOrderDto tmsTransportOrderDto = getTmsTransportOrderDto(omsOrder);
        tmsServiceClient.driverAssignment(tmsTransportOrderDto);

        wmsServiceClient.stockUnlock(orderId);

        redisService.delete(RedisConstant.ORDER_KEY_PREFIX + orderId);
        return true;
    }

    @Override
    public boolean markOrderPaymentTimeout(String orderId) {
        int rows = omsOrderDao.update(null, new LambdaUpdateWrapper<OmsOrder>()
                .eq(OmsOrder::getOrderId, orderId)
                .in(OmsOrder::getStatus, (short) 2, (short) 3)
                .set(OmsOrder::getStatus, (short) 4));
        if (rows > 0) {
            redisService.delete(RedisConstant.ORDER_KEY_PREFIX + orderId);
        }
        return rows > 0;
    }

    @Override
    public Map<String, Object> getOrderPayDeadline(String orderId) {
        String key = RedisConstant.ORDER_KEY_PREFIX + orderId;
        Long ttl = redisTemplate.getExpire(key, TimeUnit.SECONDS);
        long remainingSeconds = (ttl == null || ttl <= 0) ? 0L : ttl;
        Map<String, Object> payload = new HashMap<>();
        payload.put("orderId", orderId);
        payload.put("remainingSeconds", remainingSeconds);
        payload.put("expireAt", remainingSeconds > 0 ? System.currentTimeMillis() + remainingSeconds * 1000 : null);
        return payload;
    }

    private static TmsTransportOrderDto getTmsTransportOrderDto(OmsOrder omsOrder) {
        String warehouseCity = omsOrder.getWarehouseCity();
        String city = omsOrder.getCity();

        BigDecimal total = omsOrder.getPrice() == null ? BigDecimal.ZERO : omsOrder.getPrice();

        //平台抽成
        BigDecimal platform = total.multiply(new BigDecimal("0.10")).setScale(2, RoundingMode.HALF_UP);
        //司机运费
        BigDecimal fee = total.multiply(new BigDecimal("0.20")).setScale(2, RoundingMode.HALF_UP);
        //商家真实收款
        BigDecimal merchantHarvest = total.multiply(new BigDecimal("0.70")).setScale(2, RoundingMode.HALF_UP);

        TmsTransportOrderDto tmsTransportOrderDto = new TmsTransportOrderDto();
        tmsTransportOrderDto.setGoodsId(omsOrder.getGoodsId());
        tmsTransportOrderDto.setOrderId(omsOrder.getOrderId());
        tmsTransportOrderDto.setPhone(omsOrder.getUserPhone());
        tmsTransportOrderDto.setOrigin(warehouseCity);
        tmsTransportOrderDto.setDest(city);
        tmsTransportOrderDto.setFee(fee);
        return tmsTransportOrderDto;
    }

    private String queryTransportOrderIdByOrderId(String orderId) {
        if (orderId == null || orderId.isBlank()) {
            return null;
        }
        try {
            CommonResult<?> result = tmsServiceClient.getTransportOrderIdByOrderId(orderId);
            Object data = result == null ? null : result.getData();
            if (data == null) {
                return null;
            }
            String value = String.valueOf(data).trim();
            return value.isEmpty() ? null : value;
        } catch (Exception ignored) {
            return null;
        }
    }
}

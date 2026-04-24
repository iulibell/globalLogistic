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
import com.oms.dao.OmsOrderDao;
import com.oms.dto.OmsOrderDto;
import com.oms.dto.TmsTransportOrderDto;
import com.oms.entity.OmsOrder;
import com.oms.service.OmsOrderService;
import com.oms.service.client.TmsServiceClient;
import com.oms.service.client.WmsServiceClient;
import com.service.RedisService;
import jakarta.annotation.Resource;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Service
public class OmsOrderServiceImpl implements OmsOrderService {
    @Resource
    private OmsOrderDao omsOrderDao;
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

    @Override
    @Transactional
    @GlobalTransactional(name = "oms-add-order", rollbackFor = Exception.class)
    public void addOrder(OmsOrderDto omsOrderDto) {
        OmsOrder omsOrder = new OmsOrder();
        BeanUtil.copyProperties(omsOrderDto,omsOrder);
        omsOrder.setStatus((short) 3);
        omsOrderDao.insert(omsOrder);

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
        return omsOrderDto;
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
        tmsTransportOrderDto.setPhone(omsOrder.getUserPhone());
        tmsTransportOrderDto.setOrigin(warehouseCity);
        tmsTransportOrderDto.setDest(city);
        tmsTransportOrderDto.setFee(fee);
        return tmsTransportOrderDto;
    }
}

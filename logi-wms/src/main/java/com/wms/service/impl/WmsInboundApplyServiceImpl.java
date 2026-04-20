package com.wms.service.impl;

import cn.dev33.satoken.stp.StpUtil;
import cn.ipokerface.snowflake.SnowflakeIdGenerator;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.constant.RabbitConstant;
import com.constant.RedisConstant;
import com.service.RedisService;
import com.wms.dao.WmsInboundApplyDao;
import com.wms.dao.WmsInboundDao;
import com.wms.dao.WmsInboundItemDao;
import com.wms.dto.TmsTransportOrderDto;
import com.wms.dto.WmsInboundApplyDto;
import com.wms.entity.WmsInbound;
import com.wms.entity.WmsInboundApply;
import com.wms.entity.WmsInboundItem;
import com.wms.service.WmsInboundApplyService;
import com.wms.service.client.TmsServiceClient;
import jakarta.annotation.Resource;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Service
public class WmsInboundApplyServiceImpl implements WmsInboundApplyService {
    @Resource
    private WmsInboundDao wmsInboundDao;
    @Resource
    private WmsInboundItemDao wmsInboundItemDao;
    @Resource
    private WmsInboundApplyDao wmsInboundApplyDao;
    @Resource
    private SnowflakeIdGenerator snowflakeIdGenerator;
    @Resource
    private RabbitTemplate rabbitTemplate;
    @Resource
    private RedisService redisService;
    @Resource
    private TmsServiceClient tmsServiceClient;

    @Override
    @Transactional
    public void addInboundApply(WmsInboundApplyDto wmsInboundApplyDto) {
        WmsInboundApply wmsInboundApply = new WmsInboundApply();
        BeanUtils.copyProperties(wmsInboundApplyDto,wmsInboundApply);
        wmsInboundApplyDao.insert(wmsInboundApply);
    }

    @Override
    public List<WmsInboundApplyDto> getInboundApply(int pageNum, int pageSize) {
        StpUtil.checkPermission("keeper");
        StpUtil.checkLogin();
        IPage<WmsInboundApply> page = new Page<>(pageNum,pageSize);
        wmsInboundApplyDao.selectPage(page,null);
        return page.convert(wmsInboundApply -> {
            WmsInboundApplyDto wmsInboundApplyDto = new WmsInboundApplyDto();
            BeanUtils.copyProperties(wmsInboundApply,wmsInboundApplyDto);
            return wmsInboundApplyDto;
        }).getRecords();
    }

    @Override
    public WmsInboundApplyDto getInboundApplyById(String applyId) {
        WmsInboundApplyDto wmsInboundApplyDto = new WmsInboundApplyDto();
        BeanUtils.copyProperties(wmsInboundApplyDao.selectOne(new LambdaQueryWrapper<WmsInboundApply>()
                .eq(WmsInboundApply::getApplyId,applyId)),wmsInboundApplyDto);
        return wmsInboundApplyDto;
    }

    @Override
    @Transactional
    public void accessInboundApply(String applyId, BigDecimal fee) {
        StpUtil.checkPermission("keeper");
        StpUtil.checkLogin();
        wmsInboundApplyDao.update(new LambdaUpdateWrapper<WmsInboundApply>()
                .eq(WmsInboundApply::getApplyId,applyId)
                .set(WmsInboundApply::getStatus,(short)2)
                .set(WmsInboundApply::getFee,fee));
        rabbitTemplate.convertAndSend(RabbitConstant.INBOUND_TTL_EXCHANGE,
                RabbitConstant.INBOUND_TTL_ROUTING_KEY,
                applyId);
        redisService.set(RedisConstant.INBOUND_KEY_PREFIX + applyId,
                "",
                RedisConstant.INBOUND_EXPIRE_TIME,
                TimeUnit.HOURS);
    }

    @Override
    @Transactional
    public void rejectInboundApply(String applyId, String remark) {
        StpUtil.checkPermission("keeper");
        StpUtil.checkLogin();
        wmsInboundApplyDao.update(new LambdaUpdateWrapper<WmsInboundApply>()
                .eq(WmsInboundApply::getApplyId,applyId)
                .set(WmsInboundApply::getStatus,(short)1)
                .set(WmsInboundApply::getRejectRemark,remark));
    }

    @Override
    @Transactional
    public void payForInbound(String applyId) {
        String inboundId = snowflakeIdGenerator.nextId() + Arrays.toString(applyId.getBytes());

        int paid = wmsInboundApplyDao.update(null, new LambdaUpdateWrapper<WmsInboundApply>()
                .eq(WmsInboundApply::getApplyId, applyId)
                .eq(WmsInboundApply::getStatus, (short) 2)
                .set(WmsInboundApply::getStatus, (short) 4));
        if (paid == 0) {
            return;
        }
        WmsInboundApply wmsInboundApply = wmsInboundApplyDao
                .selectOne(new LambdaQueryWrapper<WmsInboundApply>()
                        .eq(WmsInboundApply::getApplyId,applyId));

        WmsInbound wmsInbound = new WmsInbound();
        WmsInboundItem wmsInboundItem = new WmsInboundItem();
        BeanUtils.copyProperties(wmsInboundApply,wmsInbound);
        BeanUtils.copyProperties(wmsInboundApply,wmsInboundItem);
        wmsInboundItem.setInboundId(inboundId);
        wmsInbound.setInboundId(inboundId);
        wmsInbound.setQuantity(wmsInboundApply.getApplyQuantity());

        redisService.delete(RedisConstant.INBOUND_KEY_PREFIX + applyId);

        wmsInboundDao.insert(wmsInbound);
        wmsInboundItemDao.insert(wmsInboundItem);

        TmsTransportOrderDto tmsTransportOrderDto = getTmsTransportOrderDto(wmsInboundApply);

        tmsServiceClient.driverAssignment(tmsTransportOrderDto);
    }

    @Override
    public boolean markInboundApplyPaymentTimeout(String applyId) {
        int rows = wmsInboundApplyDao.update(null, new LambdaUpdateWrapper<WmsInboundApply>()
                .eq(WmsInboundApply::getApplyId, applyId)
                .eq(WmsInboundApply::getStatus, (short) 2)
                .set(WmsInboundApply::getStatus, (short) 3));
        if (rows > 0) {
            redisService.delete(RedisConstant.INBOUND_KEY_PREFIX + applyId);
        }
        return rows > 0;
    }

    private static TmsTransportOrderDto getTmsTransportOrderDto(WmsInboundApply wmsInboundApply) {
        String warehouseCity = wmsInboundApply.getWarehouseCity();
        String city = wmsInboundApply.getCity();
        BigDecimal total = wmsInboundApply.getFee() == null ? BigDecimal.ZERO : wmsInboundApply.getFee();

        //平台抽成
        BigDecimal platform = total.multiply(new BigDecimal("0.70")).setScale(2, RoundingMode.HALF_UP);
        //司机运费
        BigDecimal fee = total.multiply(new BigDecimal("0.30")).setScale(2, RoundingMode.HALF_UP);

        TmsTransportOrderDto tmsTransportOrderDto = new TmsTransportOrderDto();
        tmsTransportOrderDto.setOrigin(city);
        tmsTransportOrderDto.setDest(warehouseCity);
        tmsTransportOrderDto.setFee(fee);
        return tmsTransportOrderDto;
    }

}

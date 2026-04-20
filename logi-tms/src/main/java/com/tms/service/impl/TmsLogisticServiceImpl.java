package com.tms.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.constant.RedisConstant;
import com.service.RedisService;
import com.tms.dao.TmsLogisticDao;
import com.tms.dao.TmsTransportOrderDao;
import com.tms.dto.TmsLogisticDto;
import com.tms.entity.TmsLogistic;
import com.tms.entity.TmsTransportOrder;
import com.tms.service.TmsLogisticService;
import jakarta.annotation.Resource;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Service
public class TmsLogisticServiceImpl implements TmsLogisticService {
    @Resource
    private TmsLogisticDao tmsLogisticDao;
    @Resource
    private TmsTransportOrderDao tmsTransportOrderDao;
    @Resource
    private RedisService redisService;

    @Override
    public List<TmsLogisticDto> getLogisticById(String transportId) {
        Object cacheObj = redisService.get(RedisConstant
                .LOGI_KEY_PREFIX + transportId);
        if (cacheObj != null) {
            if ("EMPTY".equals(cacheObj)) {
                return null;
            }
            if (cacheObj instanceof List<?>) {
                return (List<TmsLogisticDto>) cacheObj;
            }
            // 兼容旧缓存：历史版本缓存的是单个 TmsLogisticDto
            if (cacheObj instanceof TmsLogisticDto dto) {
                return Collections.singletonList(dto);
            }
            // 未知旧值格式，回源数据库重建缓存
        }

        List<TmsLogistic> logistics = tmsLogisticDao.selectList(new LambdaQueryWrapper<TmsLogistic>()
                .eq(TmsLogistic::getTransportOrderId, transportId)
                .orderByAsc(TmsLogistic::getCreateTime, TmsLogistic::getId));

        if (logistics == null || logistics.isEmpty()) {
            redisService.set(RedisConstant.LOGI_KEY_PREFIX + transportId,
                    "EMPTY",
                    RedisConstant.LOGI_EXPIRE_TIME,
                    TimeUnit.MINUTES);
            return null;
        }

        List<TmsLogisticDto> dtoList = new ArrayList<>();
        for (TmsLogistic logistic : logistics) {
            TmsLogisticDto dto = new TmsLogisticDto();
            BeanUtils.copyProperties(logistic, dto);
            dtoList.add(dto);
        }
        TmsTransportOrder order = tmsTransportOrderDao.selectOne(new LambdaQueryWrapper<TmsTransportOrder>()
                .eq(TmsTransportOrder::getTransportOrderId, transportId)
                .orderByDesc(TmsTransportOrder::getId)
                .last("limit 1"));
        if (order != null) {
            for (TmsLogisticDto dto : dtoList) {
                dto.setOrigin(order.getOrigin());
                dto.setDest(order.getDest());
            }
        }

        redisService.set(RedisConstant.LOGI_KEY_PREFIX + transportId,
                dtoList,
                RedisConstant.LOGI_EXPIRE_TIME,
                TimeUnit.MINUTES);

        return dtoList;
    }
}

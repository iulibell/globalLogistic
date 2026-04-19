package com.tms.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.constant.RedisConstant;
import com.service.RedisService;
import com.tms.dao.TmsLogisticDao;
import com.tms.dto.TmsLogisticDto;
import com.tms.entity.TmsLogistic;
import com.tms.service.TmsLogisticService;
import jakarta.annotation.Resource;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
public class TmsLogisticServiceImpl implements TmsLogisticService {
    @Resource
    private TmsLogisticDao tmsLogisticDao;
    @Resource
    private RedisService redisService;

    @Override
    public TmsLogisticDto getLogisticById(String transportId) {
        Object cacheObj = redisService.get(RedisConstant
                .LOGI_KEY_PREFIX + transportId);
        if (cacheObj != null) {
            if ("EMPTY".equals(cacheObj)) {
                return null;
            }
            return (TmsLogisticDto) cacheObj;
        }

        TmsLogistic logistic = tmsLogisticDao.selectOne(new LambdaQueryWrapper<TmsLogistic>()
                .eq(TmsLogistic::getTransportOrderId, transportId));

        if (logistic == null) {
            redisService.set(RedisConstant.LOGI_KEY_PREFIX + transportId,
                    "EMPTY",
                    RedisConstant.LOGI_EXPIRE_TIME,
                    TimeUnit.MINUTES);
            return null;
        }

        TmsLogisticDto tmsLogisticDto = new TmsLogisticDto();
        BeanUtils.copyProperties(logistic, tmsLogisticDto);

        redisService.set(RedisConstant.LOGI_KEY_PREFIX + transportId,
                tmsLogisticDto,
                RedisConstant.LOGI_EXPIRE_TIME,
                TimeUnit.MINUTES);

        return tmsLogisticDto;
    }
}

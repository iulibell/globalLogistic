package com.tms.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.tms.dao.TmsLogisticDao;
import com.tms.dto.TmsLogisticDto;
import com.tms.entity.TmsLogistic;
import com.tms.service.TmsLogisticService;
import jakarta.annotation.Resource;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

@Service
public class TmsLogisticServiceImpl implements TmsLogisticService {
    @Resource
    private TmsLogisticDao tmsLogisticDao;

    @Override
    public TmsLogisticDto getLogisticById(String transportId) {
        TmsLogisticDto tmsLogisticDto = new TmsLogisticDto();
        BeanUtils.copyProperties(tmsLogisticDao.selectOne(new LambdaQueryWrapper<TmsLogistic>()
                .eq(TmsLogistic::getTransportOrderId,transportId)),tmsLogisticDto);
        return tmsLogisticDto;
    }
}

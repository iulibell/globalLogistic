package com.tms.service.impl;

import com.tms.dao.TmsVehicleDao;
import com.tms.dto.TmsVehicleDto;
import com.tms.entity.TmsVehicle;
import com.tms.service.TmsVehicleService;
import jakarta.annotation.Resource;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

@Service
public class TmsVehicleServiceImpl implements TmsVehicleService {
    @Resource
    private TmsVehicleDao tmsVehicleDao;

    @Override
    public void addVehicle(TmsVehicleDto tmsVehicleDto) {
        TmsVehicle tmsVehicle = new TmsVehicle();
        BeanUtils.copyProperties(tmsVehicleDto,tmsVehicle);
        //后续可添加审核机制
        tmsVehicleDao.insert(tmsVehicle);
    }
}

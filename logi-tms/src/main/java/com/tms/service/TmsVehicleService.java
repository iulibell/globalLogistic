package com.tms.service;

import com.tms.dto.TmsVehicleDto;

public interface TmsVehicleService {
    /**
     * 添加货车信息(driver操作)
     * @param tmsVehicleDto 货车dto
     */
    void addVehicle(TmsVehicleDto tmsVehicleDto);
}

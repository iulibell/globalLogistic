package com.tms.service;

import com.tms.dto.TmsVehicleDto;

/**
 * TMS 车辆管理服务接口。
 */
public interface TmsVehicleService {
    /**
     * 添加货车信息(driver操作)
     *
     * @param tmsVehicleDto 货车dto
     */
    void addVehicle(TmsVehicleDto tmsVehicleDto);
}

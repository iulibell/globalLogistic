package com.tms.service;

import com.tms.dto.TmsLogisticDto;

public interface TmsLogisticService {
    /**
     * 通过运输单查询物流(全用户可查)
     *
     * @param transportId 运输单id
     * @return 运输单信息
     */
    TmsLogisticDto getLogisticById(String transportId);
}

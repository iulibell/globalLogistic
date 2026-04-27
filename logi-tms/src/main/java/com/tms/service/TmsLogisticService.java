package com.tms.service;

import com.tms.dto.TmsLogisticDto;

import java.util.List;

/**
 * TMS 物流轨迹查询服务接口。
 */
public interface TmsLogisticService {
    /**
     * 通过运输单查询物流(全用户可查)
     *
     * @param transportId 运输单id
     * @return 运输单信息
     */
    List<TmsLogisticDto> getLogisticById(String transportId);
}

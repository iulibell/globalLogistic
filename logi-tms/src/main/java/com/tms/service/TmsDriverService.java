package com.tms.service;

import com.tms.dto.TmsTransportOrderDto;

import java.util.List;

public interface TmsDriverService {

    /**
     * 将运输派单列表分配到司机页面
     *
     * @param pageNum 页数
     * @param pageSize 页大小
     * @param currentCity 目前司机所在的城市
     * @return 派单列表
     */
    List<TmsTransportOrderDto> getTransportOrder(int pageNum, int pageSize, String currentCity);

    /**
     * 接收派单(driver操作)
     *
     * @param transportOrderId 派单id
     * @param driverId 司机id
     */
    boolean accessAssignment(String transportOrderId , String driverId);

    /**
     * 确认发车(driver操作)
     * @param transportOrderId 运输单id
     */
    void confirmDeparture(String transportOrderId,String city);

    /**
     * 确认送达(driver操作)
     * @param transportOrderId 运输单id
     */
    void confirmArrived(String transportOrderId);

    /**
     * 确认签收(driver操作)
     * @param transportOrderId 运输单id
     */
    void confirmReceived(String transportOrderId);
}

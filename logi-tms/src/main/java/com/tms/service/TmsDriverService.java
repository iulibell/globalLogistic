package com.tms.service;

import com.tms.dto.TmsDriverDto;
import com.tms.dto.TmsAssignedOrderDto;
import com.tms.dto.TmsCurrentAssignmentDto;
import com.tms.dto.TmsTransportOrderDto;

import java.util.List;

public interface TmsDriverService {

    /**
     * 将运输派单列表分配到司机页面
     *
     * @param pageNum 页数
     * @param pageSize 页大小
     * @return 派单列表
     */
    List<TmsTransportOrderDto> getTransportOrder(int pageNum, int pageSize);

    /**
     * 获取当前登录司机信息(driver操作)
     * @return 当前司机信息
     */
    TmsDriverDto getCurrentDriverInfo();

    /**
     * 获取当前司机待处理的人工派单提示
     */
    List<TmsAssignedOrderDto> getPendingAssignedOrders();

    /**
     * 获取当前司机已接受的人工派单
     */
    List<TmsAssignedOrderDto> getCurrentAssignedOrders();

    /**
     * 获取当前司机正在处理的当前派单详情
     */
    TmsCurrentAssignmentDto getCurrentAssignmentDetail();

    /**
     * 接受一条人工派单提示（仅修改提示状态）
     */
    boolean acceptAssignedOrder(String transportId);

    /**
     * 拒绝一条人工派单提示（触发拒单扣权重逻辑）
     */
    boolean rejectAssignedOrder(String transportId);

    /**
     * 获取空闲的司机列表
     * @param pageNum 页数
     * @param pageSize 页大小
     * @return 空闲司机列表
     */
    List<TmsDriverDto> getAvailableDriver(int pageNum, int pageSize);

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

    /**
     * 修改状态(driver操作)
     * @param status 状态
     */
    void updateStatus(String driverId, Short status);
}

package com.tms.service;

import com.tms.dto.TmsTransportOrderDto;

import java.util.List;

public interface TmsTransportOrderService {

    /**
     * 分配司机(待确认接单)
     *
     * @param tmsTransportOrderDto 运输订单dto
     */
    void driverAssignment(TmsTransportOrderDto tmsTransportOrderDto);

    /**
     * 获取需手动分配司机的运输订单列表
     *
     * @param pageNum 页数
     * @param pageSize 页大小
     * @return 运输单列表
     */
    List<TmsTransportOrderDto> getManualAssignment(int pageNum, int pageSize);

    /**
     * 派单窗口到期（死信）：若运输单仍为待接单(0)，则运费加价、重新写入接单 Redis 与延时队列，进入下一轮派单。
     * 已接单或已非待接单状态则不修改（幂等，防重复消息）。
     *
     * @param transportOrderId 运输单id
     * @return 是否执行了加价重派
     */
    boolean handleAssignWindowExpired(String transportOrderId);

    /**
     * 司机拒单。
     * <ul>
     *   <li>待接单(0)：{@code driverId} 传 {@code null}；累加拒绝次数并按次数重开派单窗口或转入待人工分配。</li>
     *   <li>已接单(1)且为管理员人工指派：须传与当前订单 {@code driverId} 一致的 {@code driverId}；记录拒单司机、扣减该司机权重，订单回到待人工分配(6)。</li>
     * </ul>
     *
     * @param transportOrderId 运输单id
     * @param driverId         拒单司机用户id；待接单池拒单时传 {@code null}
     * @return 是否完成一次有效拒单处理
     */
    boolean recordDriverReject(String transportOrderId, String driverId);

    /**
     * 管理员在待人工分配司机状态下指定接单司机。
     *
     * @param transportOrderId 运输单id
     * @param driverId         司机用户id
     * @return 是否指派成功
     */
    boolean manualAssignDriver(String transportOrderId, String driverId);
}

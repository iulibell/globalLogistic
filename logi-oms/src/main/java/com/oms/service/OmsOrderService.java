package com.oms.service;

import com.oms.dto.OmsOrderDto;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * OMS 订单服务接口。
 * <p>
 * 对外提供订单创建、支付流转、查询与超时处理能力。
 */
public interface OmsOrderService {
    /**
     * 添加订单,若为普通商品(category->0)则直接进行锁库操作,并待用户支付(30mins),电商调用该接口
     * @param omsOrderDto 订单dto
     */
    void addOrder(OmsOrderDto omsOrderDto);

    /**
     * 获取订单列表(manager操作)
     * @param pageNum 页数
     * @param pageSize 页大小
     * @return 订单列表
     */
    List<OmsOrderDto> getOrder(int pageNum, int pageSize);

    /**
     * 系统间调用：按用户分页查询订单（无 manager 权限校验）
     */
    List<OmsOrderDto> getOrderByUserForSys(String userId, int pageNum, int pageSize);

    /**
     * 根据订单id获取订单信息(manager操作)
     * @param orderId 订单id
     * @return 单个订单信息
     */
    OmsOrderDto getOrderById(String orderId);

    /**
     * 系统间调用：按订单id获取订单信息（无manager权限校验）
     * @param orderId 订单id
     * @return 单个订单信息
     */
    OmsOrderDto getOrderByIdForSys(String orderId);

    /**
     * 删除超时订单(message模块调用)
     * @param orderId 订单id
     */
    void deleteOrder(String orderId);

    /**
     * 商家对申请订单进行取消
     * @param orderId 申请订单id
     */
    void cancelOrder(String orderId);

    /**
     * 商家对上架申请进行支付
     * @param orderId 申请订单id
     */
    boolean payForOrder(String orderId);

    /**
     * 支付超时：仅当订单仍为「已审核(2)」或「待支付(3)」时置为「超时未支付(4)」。已支付、已超时、已取消/已删单则不修改。
     *
     * @param orderId 订单id
     * @return 本次是否将状态更新为 4
     */
    boolean markOrderPaymentTimeout(String orderId);

    /**
     * 系统间调用：获取订单支付截止信息（基于 Redis TTL）
     */
    Map<String, Object> getOrderPayDeadline(String orderId);

    /**
     * 统计用户在活动时间内、指定商品已下单（待支付/已支付）的件数，用于秒杀限购。
     */
    int sumItemQuantityForUserGoodsBetween(String userId, String goodsId, Date startTime, Date endTime);
}

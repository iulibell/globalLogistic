package com.message.service;

public interface MqMessageLogService {
    /**
     * 记录订单支付超时日志
     * @param orderId 订单id
     */
    void saveOrderTimeoutLog(String orderId);

    /**
     * 记录订单死信队列消费失败日志
     * @param orderId 订单id
     * @param message 消息
     */
    void saveOrderTimeoutFailedLog(String orderId, String message);

    /**
     * 记录入库订单支付超时日志
     * @param applyId 申请id
     */
    void saveInboundTimeoutLog(String applyId);

    /**
     * 记录入库订单死信队列消费失败日志
     * @param applyId 入库订单id
     * @param message 消息
     */
    void saveInboundTimeoutFailedLog(String applyId, String message);

    /**
     * 记录派单接收超时日志
     * @param transportOrderId 派单id
     */
    void saveAssignTimeoutLog(String transportOrderId);

    /**
     * 记录派单死信队列消费失败日志
     * @param transportOrderId 派单id
     * @param message 消息
     */
    void saveAssignTimeoutFailedLog(String transportOrderId, String message);
}

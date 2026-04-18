package com.admin.service;

import com.api.CommonResult;

public interface ManagerService {
    /**
     * 调用omsClient获取，减少耦合，获取订单列表
     * @param pageNum 页数
     * @param pageSize 页大小
     * @return 订单列表
     */
    CommonResult<?> getOrder(int pageNum, int pageSize);

    /**
     * 调用omsClient获取，减少耦合，根据订单id获取单个订单
     * @param orderId 订单id
     * @return 单个订单
     */
    CommonResult<?> getOrderById(String orderId);
}

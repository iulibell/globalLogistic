package com.wms.service;

import com.wms.dto.OmsOrderDto;

/**
 * WMS 库存锁定服务接口。
 * <p>
 * 面向 OMS 下单链路，提供锁库与解锁能力。
 */
public interface WmsStockLockService {
    /**
     * 执行库存锁定。
     *
     * @param omsOrderDto 订单关联dto
     */
    void WmsStockLock(OmsOrderDto omsOrderDto);

    /**
     * 执行库存解锁。
     *
     * @param orderId 订单关联id
     */
    void WmsStockUnlock(String orderId);
}

package com.wms.service;

import com.wms.dto.OmsOrderDto;

public interface WmsStockLockService {
    /**
     * 进行商品锁操作
     *
     * @param wmsStockDto 库存dto
     */
    void WmsStockLock(OmsOrderDto omsOrderDto);

    /**
     * 对库存进行解锁操作
     *
     * @param orderId 订单关联id
     */
    void WmsStockUnlock(String orderId);
}

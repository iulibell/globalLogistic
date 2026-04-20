package com.wms.controller;

import com.wms.dto.OmsOrderDto;
import com.wms.service.WmsStockLockService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/wms")
@Tag(name = "WmsStockController", description = "仓储库存锁：与 OMS 订单审核、取消联动，防止超卖。")
public class WmsStockController {
    @Resource
    private WmsStockLockService wmsStockLockService;

    @PostMapping("/wmsStockLock")
    @Operation(
            summary = "按订单锁定库存",
            description = "订单审核通过等场景由 OMS 调用，根据订单 SKU 与数量扣减可售库存。")
    public void stockLock(@RequestBody OmsOrderDto omsOrderDto) {
        wmsStockLockService.WmsStockLock(omsOrderDto);
    }

    @PostMapping("/wmsStockUnlock")
    @Operation(
            summary = "按订单号解锁库存",
            description = "审核退回或订单取消时释放已占用的库存数量。")
    public void stockUnlock(@RequestParam String orderId) {
        wmsStockLockService.WmsStockUnlock(orderId);
    }
}

package com.wms.controller;

import com.api.CommonResult;
import com.wms.dto.OmsOrderDto;
import com.wms.dto.WmsStockDto;
import com.wms.service.WmsStockLockService;
import com.wms.service.WmsStockService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/wms")
@Tag(name = "WmsStockController", description = "仓储库存锁：与 OMS 订单审核、取消联动，防止超卖。")
public class WmsStockController {
    @Resource
    private WmsStockLockService wmsStockLockService;
    @Resource
    private WmsStockService wmsStockService;

    @GetMapping("/keeper/getStock")
    @Operation(summary = "获取库存列表",description = "获取库存分页列表")
    public CommonResult<?> getStock(@RequestParam(defaultValue = "1")int pageNum,
                                    @RequestParam(defaultValue = "10")int pageSize){
        return CommonResult.success(wmsStockService.getStock(pageNum,pageSize));
    }

    @GetMapping("/keeper/getStockById")
    @Operation(summary = "获取单个库存",description = "获取单个库存")
    public CommonResult<?> getStockById(@RequestParam String stockId){
        return CommonResult.success(wmsStockService.getStockById(stockId));
    }

    @PostMapping("/keeper/addStock")
    @Operation(description = "添加库存")
    public CommonResult<?> addStock(@RequestBody WmsStockDto wmsStockDto){
        wmsStockService.addStock(wmsStockDto);
        return CommonResult.success("添加成功");
    }

    @PostMapping("/keeper/updateStock")
    @Operation(description = "更新库存")
    public CommonResult<?> updateStock(@RequestBody WmsStockDto wmsStockDto){
        wmsStockService.updateStock(wmsStockDto);
        return CommonResult.success("更新成功!");
    }

    @PostMapping("/keeper/deleteStock")
    @Operation(description = "删除库存")
    public CommonResult<?> deleteStock(@RequestParam String stockId){
        wmsStockService.deleteStock(stockId);
        return CommonResult.success("删除成功!");
    }

    @PostMapping("/sys/wmsStockLock")
    @Operation(
            summary = "按订单锁定库存",
            description = "订单审核通过等场景由 OMS 调用，根据订单 SKU 与数量扣减可售库存。")
    public void stockLock(@RequestBody OmsOrderDto omsOrderDto) {
        wmsStockLockService.WmsStockLock(omsOrderDto);
    }

    @PostMapping("/sys/wmsStockUnlock")
    @Operation(
            summary = "按订单号解锁库存",
            description = "审核退回或订单取消时释放已占用的库存数量。")
    public void stockUnlock(@RequestParam String orderId) {
        wmsStockLockService.WmsStockUnlock(orderId);
    }
}

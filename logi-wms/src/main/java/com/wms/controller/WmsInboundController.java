package com.wms.controller;

import com.api.CommonResult;
import com.wms.service.WmsInboundApplyService;
import com.wms.service.WmsInboundService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

@RestController
@RequestMapping("/wms")
@Tag(name = "WmsInboundController", description = "入库单与入库申请：查询、确认入库及支付超时标记。")
public class WmsInboundController {
    @Resource
    private WmsInboundApplyService wmsInboundApplyService;
    @Resource
    private WmsInboundService wmsInboundService;

    @GetMapping("/keeper/getInboundApply")
    @Operation(summary = "获取入库申请单",description = "返回入库申请分页列表。")
    public CommonResult<?> getInboundApply(@RequestParam(defaultValue = "1")int pageNum,
                                           @RequestParam(defaultValue = "10")int pageSize){
        return CommonResult.success(wmsInboundApplyService.getInboundApply(pageNum,pageSize));
    }

    @GetMapping("/keeper/getInboundApplyById")
    @Operation(summary = "获取单个申请入库单",description = "返回单个入库申请单。")
    public CommonResult<?> getInboundApplyById(@RequestParam String applyId){
        return CommonResult.success(wmsInboundApplyService.getInboundApplyById(applyId));
    }

    @PostMapping("/keeper/accessInboundApply")
    @Operation(summary = "入库申请审核通过",description = "按入库申请单号修改入库申请状态")
    public void accessInboundApply(@RequestParam String applyId,
                                   @RequestParam BigDecimal fee){
        wmsInboundApplyService.accessInboundApply(applyId, fee);
    }

    @PostMapping("/keeper/rejectInboundApply")
    @Operation(summary = "入库申请退回",description = "按入库申请单号修改入库申请状态")
    public void rejectInboundApply(@RequestParam String applyId,
                                   @RequestParam String remark){
        wmsInboundApplyService.rejectInboundApply(applyId,remark);
    }

    @GetMapping("/keeper/getInbound")
    @Operation(summary = "分页查询入库单", description = "返回入库业务分页列表。")
    public CommonResult<?> getInbound(@RequestParam(defaultValue = "1") int pageNum,
                                      @RequestParam(defaultValue = "10") int pageSize) {
        return CommonResult.success(wmsInboundService.getInbound(pageNum, pageSize));
    }

    @GetMapping("/keeper/getInboundById")
    @Operation(summary = "根据id查询入库单", description = "返回单个入库单")
    public CommonResult<?> getInboundById(@RequestParam String inboundId){
        return CommonResult.success(wmsInboundService.getInboundById(inboundId));
    }

    @PostMapping("/keeper/confirmInbound")
    @Operation(
            summary = "确认入库",
            description = "根据入库单与 SKU 确认实物入库，更新库存与单据状态。")
    public CommonResult<?> confirmInbound(@RequestParam String inboundId,
                                          @RequestParam String skuCode) {
        wmsInboundService.confirmInbound(inboundId, skuCode);
        return CommonResult.success("wms_inbound_confirmed");
    }

    @PostMapping("/sys/markInboundApplyPaymentTimeout")
    @Operation(
            summary = "标记入库申请支付超时",
            description = "系统回调：入库申请关联支付超时处理。")
    public CommonResult<Boolean> markInboundApplyPaymentTimeout(@RequestParam String applyId) {
        return CommonResult.success(wmsInboundApplyService.markInboundApplyPaymentTimeout(applyId));
    }
}

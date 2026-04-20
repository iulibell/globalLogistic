package com.wms.controller;

import com.api.CommonResult;
import com.wms.service.WmsOutboundService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/wms")
@Tag(name = "WmsOutboundController", description = "出库单：分页查询、按单号查询与确认出库。")
public class WmsOutboundController {
    @Resource
    private WmsOutboundService wmsOutboundService;

    @GetMapping("/keeper/getOutbound")
    @Operation(summary = "分页查询出库单", description = "分页返回出库任务列表。")
    public CommonResult<?> getOutbound(@RequestParam(defaultValue = "1") int pageNum,
                                       @RequestParam(defaultValue = "10") int pageSize) {
        return CommonResult.success(wmsOutboundService.getOutbound(pageNum, pageSize));
    }

    @GetMapping("/keeper/getOutboundById")
    @Operation(summary = "按出库单号查询", description = "根据 outboundId 获取单笔出库详情。")
    public CommonResult<?> getOutboundById(@RequestParam String outboundId) {
        return CommonResult.success(wmsOutboundService.getOutboundById(outboundId));
    }

    @PostMapping("/keeper/confirmOutbound")
    @Operation(summary = "确认出库", description = "完成拣货发运，更新出库单与库存流水。")
    public CommonResult<?> confirmOutbound(@RequestParam String outboundId) {
        wmsOutboundService.confirmOutbound(outboundId);
        return CommonResult.success("wms_outbound_confirmed");
    }
}

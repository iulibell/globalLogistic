package com.wms.controller;

import com.api.CommonResult;
import com.wms.service.WmsInboundApplyService;
import com.wms.service.WmsInboundService;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/wms")
public class WmsInboundController {
    @Resource
    private WmsInboundApplyService wmsInboundApplyService;
    @Resource
    private WmsInboundService wmsInboundService;

    @PostMapping("/deleteInboundApply")
    public void deleteInboundApply(@RequestParam String applyId) {
        wmsInboundApplyService.deleteInboundApply(applyId);
    }

    @GetMapping("/getInbound")
    public CommonResult<?> getInbound(@RequestParam(defaultValue = "1") int pageNum,
                                      @RequestParam(defaultValue = "10") int pageSize) {
        return CommonResult.success(wmsInboundService.getInbound(pageNum, pageSize));
    }

    @PostMapping("/confirmInbound")
    public CommonResult<?> confirmInbound(@RequestParam String inboundId,
                                          @RequestParam String skuCode) {
        wmsInboundService.confirmInbound(inboundId, skuCode);
        return CommonResult.success("已成功确认入库");
    }

    @PostMapping("/sys/markInboundApplyPaymentTimeout")
    public CommonResult<Boolean> markInboundApplyPaymentTimeout(@RequestParam String applyId) {
        return CommonResult.success(wmsInboundApplyService.markInboundApplyPaymentTimeout(applyId));
    }
}

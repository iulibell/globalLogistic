package com.wms.controller;

import com.api.CommonResult;
import com.wms.service.WmsOutboundService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/wms")
public class WmsOutboundController {
    @Resource
    private WmsOutboundService wmsOutboundService;

    @GetMapping("/getOutbound")
    public CommonResult<?> getOutbound(@RequestParam(defaultValue = "1") int pageNum,
                                       @RequestParam(defaultValue = "10") int pageSize){
        return CommonResult.success(wmsOutboundService.getOutbound(pageNum,pageSize));
    }

    @GetMapping("/getOutboundById")
    public CommonResult<?> getOutboundById(@RequestParam String outboundId){
        return CommonResult.success(wmsOutboundService.getOutboundById(outboundId));
    }

    @PostMapping("/confirmOutbound")
    public CommonResult<?> confirmOutbound(@RequestParam String outboundId){
        wmsOutboundService.confirmOutbound(outboundId);
        return CommonResult.success("已成功确认出库");
    }
}

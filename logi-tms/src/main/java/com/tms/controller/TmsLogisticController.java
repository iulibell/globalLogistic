package com.tms.controller;

import com.api.CommonResult;
import com.tms.service.TmsLogisticService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/tms")
public class TmsLogisticController {
    @Resource
    private TmsLogisticService tmsLogisticService;

    @GetMapping("/getLogisticById")
    public CommonResult<?> getLogisticById(@RequestParam String transportOrderId){
        return CommonResult.success(tmsLogisticService.getLogisticById(transportOrderId));
    }
}

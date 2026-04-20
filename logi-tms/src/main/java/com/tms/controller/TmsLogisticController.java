package com.tms.controller;

import com.api.CommonResult;
import com.tms.service.TmsLogisticService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/tms")
@Tag(name = "TmsLogisticController", description = "物流轨迹与在途信息查询。")
public class TmsLogisticController {
    @Resource
    private TmsLogisticService tmsLogisticService;

    @GetMapping("/getLogisticById")
    @Operation(
            summary = "按运输单号查询物流详情",
            description = "根据 transportOrderId 返回当前物流节点、承运信息等。")
    public CommonResult<?> getLogisticById(@RequestParam String transportOrderId) {
        return CommonResult.success(tmsLogisticService.getLogisticById(transportOrderId));
    }
}

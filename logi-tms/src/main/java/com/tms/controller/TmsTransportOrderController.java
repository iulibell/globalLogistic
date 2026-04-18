package com.tms.controller;

import com.api.CommonResult;
import com.tms.dto.TmsTransportOrderDto;
import com.tms.service.TmsTransportOrderService;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/tms")
public class TmsTransportOrderController {

    @Resource
    private TmsTransportOrderService tmsTransportOrderService;

    @PostMapping("/sys/driverAssignment")
    public void driverAssignment(@RequestBody TmsTransportOrderDto tmsTransportOrderDto) {
        tmsTransportOrderService.driverAssignment(tmsTransportOrderDto);
    }

    @PostMapping("/sys/handleAssignWindowExpired")
    public CommonResult<Boolean> handleAssignWindowExpired(@RequestParam String transportOrderId) {
        return CommonResult.success(tmsTransportOrderService.handleAssignWindowExpired(transportOrderId));
    }

    @GetMapping("/manager/getManualAssignment")
    public CommonResult<?> getManualAssignment(@RequestParam(defaultValue = "1") int pageNum,
                                               @RequestParam(defaultValue = "10") int pageSize) {
        return CommonResult.success(tmsTransportOrderService.getManualAssignment(pageNum, pageSize));
    }

    @PostMapping("/manager/manualAssignDriver")
    public CommonResult<Boolean> manualAssignDriver(@RequestParam String transportOrderId,
                                                    @RequestParam String driverId) {
        return CommonResult.success(tmsTransportOrderService.manualAssignDriver(transportOrderId, driverId));
    }
}

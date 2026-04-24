package com.tms.controller;

import com.api.CommonResult;
import com.tms.dto.TmsTransportOrderDto;
import com.tms.service.TmsTransportOrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/tms")
@Tag(name = "TmsTransportOrderController", description = "运输单：系统派单、指派窗口超时与管理员人工派车。")
public class TmsTransportOrderController {

    @Resource
    private TmsTransportOrderService tmsTransportOrderService;

    @PostMapping("/sys/driverAssignment")
    @Operation(
            summary = "系统派单给司机",
            description = "由业务系统调用，根据运输单 DTO 完成司机分配逻辑。")
    public String driverAssignment(@RequestBody TmsTransportOrderDto tmsTransportOrderDto) {
        return tmsTransportOrderService.driverAssignment(tmsTransportOrderDto);
    }

    @PostMapping("/sys/handleAssignWindowExpired")
    @Operation(
            summary = "处理派单窗口超时",
            description = "司机未在规定时间内接单时，记录或回滚指派状态。")
    public CommonResult<Boolean> handleAssignWindowExpired(@RequestParam String transportOrderId) {
        return CommonResult.success(tmsTransportOrderService.handleAssignWindowExpired(transportOrderId));
    }

    @GetMapping("/manager/getManualAssignment")
    @Operation(
            summary = "分页待人工派单列表",
            description = "管理员查看需人工干预的运输单分页。")
    public CommonResult<?> getManualAssignment(@RequestParam(defaultValue = "1") int pageNum,
                                               @RequestParam(defaultValue = "10") int pageSize) {
        return CommonResult.success(tmsTransportOrderService.getManualAssignment(pageNum, pageSize));
    }

    @PostMapping("/manager/manualAssignDriver")
    @Operation(
            summary = "人工指定司机",
            description = "管理员将运输单指派给指定 driverId。")
    public CommonResult<Boolean> manualAssignDriver(@RequestParam String transportOrderId,
                                                    @RequestParam String driverId) {
        return CommonResult.success(tmsTransportOrderService.manualAssignDriver(transportOrderId, driverId));
    }
}

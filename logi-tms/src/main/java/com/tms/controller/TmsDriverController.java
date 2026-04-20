package com.tms.controller;

import com.api.CommonResult;
import com.tms.dto.TmsVehicleDto;
import com.tms.service.TmsDriverService;
import com.tms.service.TmsTransportOrderService;
import com.tms.service.TmsVehicleService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/tms")
@Tag(name = "TmsDriverController", description = "司机端与派单相关接口")
public class TmsDriverController {

    @Resource
    private TmsTransportOrderService tmsTransportOrderService;

    @Resource
    private TmsVehicleService tmsVehicleService;

    @Resource
    private TmsDriverService tmsDriverService;

    @Operation(summary = "分页查询可用司机")
    @GetMapping("/manager/getAvailableDriver")
    public CommonResult<?> getAvailableDriver(@RequestParam(defaultValue = "1")int pageNum,
                                              @RequestParam(defaultValue = "10")int pageSize){
        return CommonResult.success(tmsDriverService.getAvailableDriver(pageNum,pageSize));
    }

    @Operation(summary = "分页查询司机运输单")
    @GetMapping("/driver/getTransportOrder")
    public CommonResult<?> getTransportOrder(@RequestParam(defaultValue = "1") int pageNum,
                                             @RequestParam(defaultValue = "10") int pageSize) {
        return CommonResult.success(tmsDriverService.getTransportOrder(pageNum, pageSize));

    }

    @Operation(summary = "查询当前司机信息")
    @GetMapping("/driver/getDriverInfo")
    public CommonResult<?> getDriverInfo() {
        return CommonResult.success(tmsDriverService.getCurrentDriverInfo());
    }

    @Operation(summary = "查询待接单任务")
    @GetMapping("/driver/getPendingAssignedOrders")
    public CommonResult<?> getPendingAssignedOrders() {
        return CommonResult.success(tmsDriverService.getPendingAssignedOrders());
    }

    @Operation(summary = "查询当前已接任务")
    @GetMapping("/driver/getCurrentAssignedOrders")
    public CommonResult<?> getCurrentAssignedOrders() {
        return CommonResult.success(tmsDriverService.getCurrentAssignedOrders());
    }

    @Operation(summary = "查询当前任务详情")
    @GetMapping("/driver/getCurrentAssignmentDetail")
    public CommonResult<?> getCurrentAssignmentDetail() {
        return CommonResult.success(tmsDriverService.getCurrentAssignmentDetail());
    }

    @Operation(summary = "确认接受派单")
    @PostMapping("/driver/acceptAssignedOrder")
    public CommonResult<Boolean> acceptAssignedOrder(@RequestParam String transportId) {
        return CommonResult.success(tmsDriverService.acceptAssignedOrder(transportId));
    }

    @Operation(summary = "拒绝已派发任务")
    @PostMapping("/driver/rejectAssignedOrder")
    public CommonResult<Boolean> rejectAssignedOrder(@RequestParam String transportId) {
        return CommonResult.success(tmsDriverService.rejectAssignedOrder(transportId));
    }

    @Operation(summary = "抢单接单")
    @PostMapping("/driver/accessAssignment")
    public CommonResult<?> accessAssignment(@RequestParam String transportOrderId,
                                            @RequestParam String driverId) {
        boolean success = tmsDriverService.accessAssignment(transportOrderId, driverId);
        if (!success) {
            return CommonResult.failed("派单已被其他司机抢走或状态不可接单");
        }
        return CommonResult.successMsg("已接单,准备好后即可发车!");
    }

    @Operation(summary = "拒绝运输单并记录原因")
    @PostMapping("/driver/rejectTransportOrder")
    public CommonResult<Boolean> rejectTransportOrder(@RequestParam String transportOrderId,
                                                      @RequestParam(required = false) String driverId) {
        return CommonResult.success(tmsTransportOrderService.recordDriverReject(transportOrderId, driverId));
    }

    @Operation(summary = "确认发车")
    @PostMapping("/driver/confirmDeparture")
    public CommonResult<?> confirmDeparture(@RequestParam String transportOrderId,
                                            @RequestParam String city) {
        tmsDriverService.confirmDeparture(transportOrderId, city);
        return CommonResult.success("已确认发车,请避免疲劳驾驶");
    }

    @Operation(summary = "确认到达")
    @PostMapping("/driver/confirmArrived")
    public CommonResult<?> confirmArrived(@RequestParam String transportOrderId) {
        tmsDriverService.confirmArrived(transportOrderId);
        return CommonResult.success("已确认送达");
    }

    @Operation(summary = "确认签收")
    @PostMapping("/driver/confirmReceived")
    public CommonResult<?> confirmReceived(@RequestParam String transportOrderId) {
        tmsDriverService.confirmReceived(transportOrderId);
        return CommonResult.success("已确认签收");
    }

    @Operation(summary = "新增司机车辆")
    @PostMapping("/driver/addVehicle")
    public CommonResult<?> addVehicle(@Valid @RequestBody TmsVehicleDto tmsVehicleDto) {
        tmsVehicleService.addVehicle(tmsVehicleDto);
        return CommonResult.successMsg("车辆信息已保存");
    }

    @Operation(summary = "更新司机在线状态")
    @PostMapping("/driver/updateStatus")
    public CommonResult<?> updateStatus(@RequestParam String driverId,
                                        @RequestParam Short status){
        tmsDriverService.updateStatus(driverId,status);
        if (status == (short)0)
        return CommonResult.success("你已上线");
            else
            return CommonResult.success("你已下线，请注意休息");
    }
}



package com.tms.controller;

import com.api.CommonResult;
import com.tms.dto.TmsVehicleDto;
import com.tms.service.TmsDriverService;
import com.tms.service.TmsTransportOrderService;
import com.tms.service.TmsVehicleService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/tms/driver")
public class TmsDriverController {

    @Resource
    private TmsTransportOrderService tmsTransportOrderService;
    @Resource
    private TmsVehicleService tmsVehicleService;
    @Resource
    private TmsDriverService tmsDriverService;

    @GetMapping("/getTransportOrder")
    public CommonResult<?> getTransportOrder(@RequestParam(defaultValue = "1") int pageNum,
                                             @RequestParam(defaultValue = "10") int pageSize,
                                             @RequestParam String currentCity){
        return CommonResult.success(tmsDriverService.getTransportOrder(pageNum,pageSize,currentCity));
    }

    @PostMapping("/accessAssignment")
    @ResponseBody
    public CommonResult<?> accessAssignment(@RequestParam String transportOrderId,
                                            @RequestParam String driverId){
        boolean success = tmsDriverService.accessAssignment(transportOrderId,driverId);
        if (!success) {
            return CommonResult.failed("派单已被其他司机抢走或状态不可接单");
        }
        return CommonResult.successMsg("已接单,准备好后即可发车!");
    }

    @PostMapping("/rejectTransportOrder")
    @ResponseBody
    public CommonResult<Boolean> rejectTransportOrder(@RequestParam String transportOrderId,
                                                      @RequestParam(required = false) String driverId) {
        return CommonResult.success(tmsTransportOrderService.recordDriverReject(transportOrderId, driverId));
    }

    @PostMapping("/confirmDeparture")
    public CommonResult<?> confirmDeparture(@RequestParam String transportOrderId,
                                            @RequestParam String city){
        tmsDriverService.confirmDeparture(transportOrderId,city);
        return CommonResult.success("已确认发车,请避免疲劳驾驶");
    }

    @PostMapping("/confirmArrived")
    public CommonResult<?> confirmArrived(@RequestParam String transportOrderId){
        tmsDriverService.confirmArrived(transportOrderId);
        return CommonResult.success("已确认送达");
    }

    @PostMapping("/confirmReceived")
    public CommonResult<?> confirmReceived(@RequestParam String transportOrderId){
        tmsDriverService.confirmReceived(transportOrderId);
        return CommonResult.success("已确认签收");
    }

    @PostMapping("/addVehicle")
    @ResponseBody
    public CommonResult<?> addVehicle(@RequestBody TmsVehicleDto tmsVehicleDto){
        tmsVehicleService.addVehicle(tmsVehicleDto);
        return CommonResult.success("车辆已添加");
    }
}

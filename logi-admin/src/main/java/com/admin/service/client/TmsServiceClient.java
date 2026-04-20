package com.admin.service.client;

import com.api.CommonResult;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * 管理员对 TMS 的远程调用（运输单人工派车、线路分页与查询等）。
 */
@FeignClient(name = "logi-tms")
public interface TmsServiceClient {

    @GetMapping("/tms/manager/getLine")
    CommonResult<?> getLine(@RequestParam(defaultValue = "1") int pageNum,
                            @RequestParam(defaultValue = "10") int pageSize);

    @GetMapping("/tms/manager/getLineById")
    CommonResult<?> getLineById(@RequestParam("lineId") Long lineId);

    @GetMapping("/tms/sys/getLineDetail")
    CommonResult<?> getLineDetail(@RequestParam("origin") String origin,
                                  @RequestParam("dest") String dest);

    @GetMapping("/tms/manager/getManualAssignment")
    CommonResult<?> getManualAssignment(@RequestParam(defaultValue = "1") int pageNum,
                                        @RequestParam(defaultValue = "10") int pageSize);

    @PostMapping("/tms/manager/manualAssignDriver")
    CommonResult<Boolean> manualAssignDriver(@RequestParam("transportOrderId") String transportOrderId,
                                             @RequestParam("driverId") String driverId);

    @GetMapping("/tms/manager/getAvailableDriver")
    CommonResult<?> getAvailableDriver(@RequestParam(defaultValue = "1") int pageNum,
                                       @RequestParam(defaultValue = "10") int pageSize);

    @PostMapping("/tms/manager/addLine")
    CommonResult<?> addLine(@RequestParam("lineId") Long lineId,
                            @RequestParam("origin") String origin,
                            @RequestParam("dest") String dest,
                            @RequestParam("estimation") Double estimation,
                            @RequestParam("status") Short status);

    @PostMapping("/tms/manager/updateLine")
    CommonResult<?> updateLine(@RequestParam("lineId") Long lineId,
                               @RequestParam("origin") String origin,
                               @RequestParam("dest") String dest,
                               @RequestParam("estimation") Double estimation,
                               @RequestParam("status") Short status);
}

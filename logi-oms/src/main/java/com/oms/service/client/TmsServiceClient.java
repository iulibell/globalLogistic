package com.oms.service.client;

import com.oms.dto.TmsTransportOrderDto;
import com.api.CommonResult;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient("logi-tms")
public interface TmsServiceClient {
    @PostMapping("/tms/sys/driverAssignment")
    void driverAssignment(@RequestBody TmsTransportOrderDto tmsTransportOrderDto);

    @GetMapping("/tms/sys/getTransportOrderIdByOrderId")
    CommonResult<?> getTransportOrderIdByOrderId(@RequestParam String orderId);
}

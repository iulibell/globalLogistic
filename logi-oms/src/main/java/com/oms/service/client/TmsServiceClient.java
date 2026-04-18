package com.oms.service.client;

import com.oms.dto.TmsTransportOrderDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient("logi-tms")
public interface TmsServiceClient {
    @PostMapping("/sys/driverAssignment")
    void driverAssignment(@RequestBody TmsTransportOrderDto tmsTransportOrderDto);
}

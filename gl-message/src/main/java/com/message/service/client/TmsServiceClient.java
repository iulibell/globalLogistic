package com.message.service.client;

import com.api.CommonResult;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient("logi-tms")
public interface TmsServiceClient {

    @PostMapping("/tms/sys/handleAssignWindowExpired")
    CommonResult<Boolean> handleAssignWindowExpired(@RequestParam("transportOrderId") String transportOrderId);
}

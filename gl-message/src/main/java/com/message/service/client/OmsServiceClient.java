package com.message.service.client;

import com.api.CommonResult;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient("logi-oms")
public interface OmsServiceClient {
    @PostMapping("/oms/sys/deleteOrder")
    void deleteOrder(@RequestParam("orderId") String orderId);

    @PostMapping("/oms/sys/markOrderPaymentTimeout")
    CommonResult<Boolean> markOrderPaymentTimeout(@RequestParam("orderId") String orderId);
}

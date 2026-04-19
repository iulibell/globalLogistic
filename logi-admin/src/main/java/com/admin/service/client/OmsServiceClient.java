package com.admin.service.client;

import com.api.CommonResult;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient("logi-oms")
public interface OmsServiceClient {
    @GetMapping("/oms/getOrder")
    CommonResult<?> getOrder(@RequestParam(defaultValue = "1") int pageNum,
                             @RequestParam(defaultValue = "10") int pageSize);
    @GetMapping("/oms/getOrderById")
    CommonResult<?> getOrderById(@RequestParam String orderId);
    @GetMapping("/oms/getOrderReview")
    CommonResult<?> getOrderReview(@RequestParam(defaultValue = "1") int pageNum,
                                   @RequestParam(defaultValue = "10") int pageSize);
    @PostMapping("/oms/accessOrderReview")
    CommonResult<?> accessOrderReview(@RequestParam String orderId,
                                      @RequestParam String remark);
    @PostMapping("/oms/rejectOrderReview")
    CommonResult<?> rejectOrderReview(@RequestParam String orderId,
                                      @RequestParam String remark);
}

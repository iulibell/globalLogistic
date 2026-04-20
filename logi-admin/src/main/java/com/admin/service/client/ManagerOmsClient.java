package com.admin.service.client;

import com.api.CommonResult;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * 管理员对 OMS 的远程调用（与 {@link OmsServiceClient} 区分，避免同服务多 Feign 默认 Bean 名冲突）。
 */
@FeignClient(name = "logi-oms", contextId = "adminManagerOmsClient")
public interface ManagerOmsClient {

    @GetMapping("/oms/getOrder")
    CommonResult<?> getOrder(@RequestParam(defaultValue = "1") int pageNum,
                             @RequestParam(defaultValue = "10") int pageSize);

    @GetMapping("/oms/getOrderById")
    CommonResult<?> getOrderById(@RequestParam String orderId);
}

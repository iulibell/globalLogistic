package com.message.service.client;

import com.api.CommonResult;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient("logi-wms")
public interface WmsServiceClient {

    @PostMapping("/wms/sys/markInboundApplyPaymentTimeout")
    CommonResult<Boolean> markInboundApplyPaymentTimeout(@RequestParam("applyId") String applyId);
}

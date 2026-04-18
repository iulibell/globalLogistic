package com.auth.client;

import org.springframework.cloud.openfeign.FeignClient;

@FeignClient("logi-oms")
public interface OmsServiceClient {
}

package com.auth.client;

import org.springframework.cloud.openfeign.FeignClient;

@FeignClient("logi-tms")
public interface TmsServiceClient {
}

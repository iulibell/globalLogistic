package com.auth.client;

import org.springframework.cloud.openfeign.FeignClient;

@FeignClient("logi-admin")
public interface AdminServiceClient {
}

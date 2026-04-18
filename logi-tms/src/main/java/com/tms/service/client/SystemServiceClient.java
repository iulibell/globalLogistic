package com.tms.service.client;

import org.springframework.cloud.openfeign.FeignClient;

@FeignClient("gl-system")
public interface SystemServiceClient {
}

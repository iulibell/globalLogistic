package com.tms.service.client;

import org.springframework.cloud.openfeign.FeignClient;

@FeignClient("logi-gl-system")
public interface SystemServiceClient {
}

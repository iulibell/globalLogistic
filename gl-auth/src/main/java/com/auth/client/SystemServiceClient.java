package com.auth.client;

import com.api.CommonResult;
import com.dto.RegisterParamDto;
import jakarta.validation.Valid;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient("gl-system")
public interface SystemServiceClient {
    @PostMapping("/register")
    public CommonResult<?> register(@Valid @RequestBody RegisterParamDto registerParamDto);
}

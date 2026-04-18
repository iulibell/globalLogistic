package com.auth.client;

import com.api.CommonResult;
import com.auth.dto.LoginRequest;
import com.dto.RegisterParamDto;
import jakarta.validation.Valid;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.Map;

@FeignClient(name = "gl-system", url = "${auth.feign.gl-system-url:http://127.0.0.1:8501}")
public interface SystemServiceClient {

    @PostMapping("/system/register")
    CommonResult<?> register(@Valid @RequestBody RegisterParamDto registerParamDto);

    @PostMapping("/system/login")
    CommonResult<Map<String, Object>> login(@Valid @RequestBody LoginRequest loginRequest);

    @PostMapping("/system/logout")
    CommonResult<?> logout();
}

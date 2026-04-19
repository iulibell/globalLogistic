package com.auth.client;

import com.api.CommonResult;
import com.auth.dto.LoginRequest;
import com.dto.RegisterParamDto;
import com.dto.SendRegisterCaptchaDto;
import jakarta.validation.Valid;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.Map;

@FeignClient("gl-system")
public interface SystemServiceClient {

    @PostMapping("/system/register")
    CommonResult<?> register(@Valid @RequestBody RegisterParamDto registerParamDto);

    @PostMapping("/system/register/sendCaptcha")
    CommonResult<?> sendRegisterCaptcha(@Valid @RequestBody SendRegisterCaptchaDto dto);

    @PostMapping("/system/login")
    CommonResult<Map<String, Object>> login(@Valid @RequestBody LoginRequest loginRequest);

    @PostMapping("/system/logout")
    CommonResult<?> logout();
}

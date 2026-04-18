package com.system.controller;

import com.api.CommonResult;
import com.dto.RegisterParamDto;
import com.system.service.RegisterService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/system")
@Tag(name = "RegisterController", description = "系统类接口")
public class RegisterController {
    @Resource
    private RegisterService registerService;

    @PostMapping("/register")
    @Operation(summary = "物流系统的用户注册接口")
    public CommonResult<?> register(@Valid @RequestBody RegisterParamDto registerParamDto) {
        registerService.register(registerParamDto);
        return CommonResult.success("注册成功,请等待审核");
    }
}

package com.system.controller;

import com.api.CommonResult;
import com.constant.AuthConstant;
import com.system.dto.LoginParamDto;
import com.system.service.LoginAndLogoutService;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/system")
public class LoginController {

    @Resource
    private LoginAndLogoutService loginAndLogoutService;

    @PostMapping("/login")
    public CommonResult<Map<String, Object>> login(@Valid @RequestBody LoginParamDto loginParamDto) {
        var tokenInfo = loginAndLogoutService.login(loginParamDto, loginParamDto.getRequiredRoleKey().trim());
        Map<String, Object> body = loginAndLogoutService.buildLoginResponse(tokenInfo, AuthConstant.TOKEN_HEAD);
        return CommonResult.success(body);
    }

    @PostMapping("/logout")
    public CommonResult<?> logout() {
        loginAndLogoutService.logout();
        return CommonResult.successMsg("已退出登录");
    }
}

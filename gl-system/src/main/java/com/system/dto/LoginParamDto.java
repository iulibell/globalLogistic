package com.system.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class LoginParamDto {
    @NotBlank(message = "用户名不能为空")
    String username;
    @NotBlank(message = "密码不能为空")
    String password;
    /**
     * 登录入口角色，与账号 userType 一致：super / manager / keeper / driver / reviewer
     */
    @NotBlank(message = "登录角色不能为空")
    private String requiredRoleKey;
}

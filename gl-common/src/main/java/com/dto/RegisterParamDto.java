package com.dto;

import com.validator.ParamValidator;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class RegisterParamDto {
    @NotBlank(message = "用户名不能为空")
    private String userName;
    @NotBlank(message = "密码不能为空")
    private String password;
    private String nickname;
    @Schema(description = "用户身份: 1 超管、2 管理员、3 仓库管理员、4 司机、5 审核员")
    @ParamValidator(value = {"1","2","3","4","5"},message = "非本平台用户")
    private String userType;
    @NotBlank(message = "手机号不能为空")
    @Size(max = 11, message = "联系方式过长")
    private String phone;
    private Short status;
}

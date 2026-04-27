package com.system.entity;

import java.util.Date;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;

import com.validator.ParamValidator;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Size;
import lombok.Data;
import com.baomidou.mybatisplus.annotation.TableName;

@Data
@TableName("sys_user")
@Schema(description = "系统用户实体")
public class SysUser {
    @TableId(type = IdType.AUTO)
    private Long id;
    @Size(max = 255)
    @Schema(description = "用户id")
    private String userId;
    @Size(max = 50)
    @Schema(description = "用户名")
    private String username;
    @Size(max = 255)
    @Schema(description = "密码(加密)")
    private String password;
    @Size(max = 50)
    @Schema(description = "用户昵称(为空则默认使用username)")
    private String nickname;
    @Size(max = 11)
    @Schema(description = "用户手机号")
    private String phone;
    @Size(max = 30)
    @Schema(description = "所在城市（仓管用于管辖仓库过滤，司机用于派单城市匹配）")
    private String city;
    @Size(max = 20)
    @Schema(description = "用户身份:1->super,2->manager,3->keeper,4->driver,5->reviewer")
    @ParamValidator(value = {"1","2","3","4","5"},message = "非本平台用户")
    private String userType;
    @Schema(description = "用户状态:0->禁用,1->启用")
    private Short status;
    private Date createTime;
    private Date updateTime;
}

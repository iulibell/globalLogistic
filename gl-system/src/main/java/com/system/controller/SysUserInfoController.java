package com.system.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/system")
@Tag(name = "SysUserInfoController",description = "用户信息自行处理接口")
public class SysUserInfoController {
}

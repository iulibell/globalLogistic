package com.system.controller;

import com.api.CommonResult;
import com.system.dto.SysUserDto;
import com.system.service.SysUserService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/system")
@Tag(name = "AdminOperationController", description = "admin对用户的信息管理接口")
public class AdminOperationController {

    @Resource
    private SysUserService sysUserService;

    @GetMapping("/admin/fetchSysUserInfo")
    public CommonResult<?> fetchSysUserInfo(@RequestParam(defaultValue = "1") int pageNum,
                                          @RequestParam(defaultValue = "10") int pageSize) {
        return CommonResult.success(sysUserService.fetchSysUserInfo(pageNum, pageSize));
    }

    @GetMapping("/admin/fetchSysUserByUserType")
    public CommonResult<?> fetchSysUserByUserType(@RequestParam(defaultValue = "1") int pageNum,
                                           @RequestParam(defaultValue = "10") int pageSize,
                                           @RequestParam String userType) {
        return CommonResult.success(sysUserService.fetchSysUserByUserType(pageNum, pageSize, userType));
    }

    @GetMapping("/admin/fetchSysUserByUserId")
    public CommonResult<?> fetchSysUserByUserId(@RequestParam String userId) {
        return CommonResult.success(sysUserService.fetchSysUserByUserId(userId));
    }

    @PostMapping("/admin/updateSysUserInfo")
    public CommonResult<?> updateSysUserInfo(@RequestBody SysUserDto sysUserDto) {
        sysUserService.updateSysUserInfo(sysUserDto);
        return CommonResult.success("已修改该用户信息!");
    }

    @PostMapping("/admin/deleteSysUser")
    public CommonResult<?> deleteSysUser(@RequestParam String userId) {
        sysUserService.deleteSysUser(userId);
        return CommonResult.success("你已删除该用户!");
    }
}

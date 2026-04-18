package com.admin.controller;

import com.admin.service.SuperService;
import com.api.CommonResult;
import com.admin.dto.SysUserDto;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/admin/super")
public class SuperController {
    @Resource
    private SuperService superService;

    @GetMapping("/fetchSysUserInfo")
    public CommonResult<?> fetchSysUserInfo(@RequestParam(defaultValue = "1")int pageNum,
                                            @RequestParam(defaultValue = "10") int pageSize){
        return CommonResult.success(superService.fetchSysUserInfo(pageNum,pageSize));
    }

    @GetMapping("/fetchSysUserByUserType")
    public CommonResult<?> fetchSysUserByUserType(@RequestParam(defaultValue = "1") int pageNum,
                                                  @RequestParam(defaultValue = "10") int pageSize,
                                                  @RequestParam String userType){
        return CommonResult.success(superService.fetchSysUserByUserType(pageNum,pageSize,userType));
    }

    @GetMapping("/fetchSysUserByUserId")
    public CommonResult<?> fetchSysUserByUserId(@RequestParam String userId){
        return CommonResult.success(superService.fetchSysUserByUserId(userId));
    }

    @PostMapping("/updateSysUserInfo")
    public CommonResult<?> updateSysUserInfo(@RequestBody SysUserDto sysUserDto){
        return superService.updateSysUserInfo(sysUserDto);
    }

    @PostMapping("/deleteSysUser")
    public CommonResult<?> deleteSysUser(@RequestParam String userId){
        return superService.deleteSysUserInfo(userId);
    }
}

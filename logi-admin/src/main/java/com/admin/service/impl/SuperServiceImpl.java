package com.admin.service.impl;

import com.admin.service.SuperService;
import com.admin.service.client.SystemServiceClient;
import com.api.CommonResult;
import com.admin.dto.SysUserDto;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

@Service
public class SuperServiceImpl implements SuperService {
    @Resource
    private SystemServiceClient systemServiceClient;

    @Override
    public CommonResult<?> fetchSysUserInfo(int pageNum, int pageSize) {
        return systemServiceClient.fetchSysUserInfo(pageNum,pageSize);
    }

    @Override
    public CommonResult<?> updateSysUserInfo(SysUserDto sysUserDto) {
        return systemServiceClient.updateSysUserInfo(sysUserDto);
    }

    @Override
    public CommonResult<?> deleteSysUserInfo(String userId) {
        return systemServiceClient.deleteSysUser(userId);
    }

    @Override
    public CommonResult<?> fetchSysUserByUserType(int pageNum, int pageSize, String userType) {
        return systemServiceClient.fetchSysUserByUserType(pageNum,pageSize,userType);
    }

    @Override
    public CommonResult<?> fetchSysUserByUserId(String userId) {
        return systemServiceClient.fetchSysUserByUserId(userId);
    }
}

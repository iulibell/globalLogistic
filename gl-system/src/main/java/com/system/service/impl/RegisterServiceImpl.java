package com.system.service.impl;

import cn.hutool.core.util.StrUtil;
import com.api.CommonResult;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.constant.RedisConstant;
import com.exception.Assert;
import com.service.RedisService;
import com.system.dao.SysUserDao;
import com.dto.RegisterParamDto;
import com.system.entity.SysUser;
import com.system.service.RegisterService;
import com.system.service.client.AdminServiceClient;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
public class RegisterServiceImpl implements RegisterService {

    @Resource
    private AdminServiceClient adminServiceClient;
    @Resource
    private RedisService redisService;
    @Resource
    private SysUserDao sysUserDao;

    @Override
    public CommonResult<String> register(RegisterParamDto registerParamDto) {
        if(StrUtil.isEmpty(registerParamDto.getUserName()) || StrUtil.isEmpty(registerParamDto.getPassword())
                || StrUtil.isEmpty(registerParamDto.getPhone())){
            Assert.fail("用户名或密码不能为空");
        }
        if(StrUtil.isEmpty(registerParamDto.getPhone())){
            Assert.fail("手机号不能为空");
        }
        if(sysUserDao.selectOne(new LambdaQueryWrapper<SysUser>()
                .eq(SysUser::getPhone, registerParamDto.getPhone())) != null){
            return CommonResult.failed("该用户已存在!");
        }
        if(redisService.get(RedisConstant.REGIS_KEY_PREFIX+registerParamDto.getPhone())==null){
            registerParamDto.setStatus((short)0);
        redisService.set(RedisConstant.REGIS_KEY_PREFIX+registerParamDto.getPhone(),
                registerParamDto,
                RedisConstant.REGIS_EXPIRE_TIME,
                TimeUnit.HOURS);
        adminServiceClient.getRegisterFromSys(registerParamDto);
        return CommonResult.success("申请成功,请等待审核!");
        }else {
            return CommonResult.failed("申请失败,你今天已发起过申请!");
        }
    }
}

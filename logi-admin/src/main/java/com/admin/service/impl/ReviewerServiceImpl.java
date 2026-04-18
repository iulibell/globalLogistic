package com.admin.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.crypto.digest.BCrypt;
import cn.ipokerface.snowflake.SnowflakeIdGenerator;
import com.admin.dao.RegisterApplicationDao;
import com.admin.entity.RegisterApplication;
import com.admin.service.ReviewerService;
import com.admin.service.client.OmsServiceClient;
import com.api.CommonResult;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.constant.RedisConstant;
import com.dto.RegisterParamDto;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.service.RedisService;
import com.system.dao.SysUserDao;
import com.system.entity.SysUser;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ReviewerServiceImpl implements ReviewerService {
    @Resource
    SysUserDao sysUserDao;
    @Resource
    RegisterApplicationDao registerApplicationDao;
    @Resource
    RedisService redisService;
    @Resource
    private OmsServiceClient omsServiceClient;
    @Resource
    SnowflakeIdGenerator snowflakeIdGenerator;

    @Transactional
    public void getRegisterFromSys(RegisterParamDto registerParamDto){
        RegisterApplication registerApplication = new RegisterApplication();
        BeanUtil.copyProperties(registerParamDto,registerApplication);
        registerApplicationDao.insert(registerApplication);
    }

    @Override
    public CommonResult<?> getOrderReview(int pageNum, int pageSize) {
        return omsServiceClient.getOrderReview(pageNum,pageSize);
    }

    @Override
    public CommonResult<?> accessOrderReview(String orderId, String remark) {
        return omsServiceClient.accessOrderReview(orderId,remark);
    }

    @Override
    public CommonResult<?> rejectOrderReview(String orderId, String remark) {
        return omsServiceClient.rejectOrderReview(orderId,remark);
    }

    public List<RegisterApplication> fetchRegisterApplication(int pageNum,int pageSize){
        IPage<RegisterApplication> page = new Page<>(pageNum, pageSize);
        return registerApplicationDao.selectPage(page,
                new LambdaQueryWrapper<>()).getRecords();
    }

    @Transactional
    public void accessRegister(RegisterParamDto registerParamDto){
        SysUser sysUser = new SysUser();
        BeanUtil.copyProperties(registerParamDto,sysUser);
        sysUser.setPassword(BCrypt.hashpw(registerParamDto.getPassword(), BCrypt.gensalt()));
        sysUser.setUserId(String.valueOf(snowflakeIdGenerator.nextId()));
        redisService.delete(RedisConstant.REGIS_KEY_PREFIX + registerParamDto.getPhone());
        sysUserDao.insert(sysUser);
        confirmReviewed(registerParamDto,(short) 1);
    }
    public void rejectRegister(RegisterParamDto registerParamDto){
        confirmReviewed(registerParamDto,(short) 2);
    }

    public void confirmReviewed(RegisterParamDto registerParamDto,short status){
        registerApplicationDao.update(new LambdaUpdateWrapper<RegisterApplication>()
                .eq(RegisterApplication::getPhone,registerParamDto.getPhone())
                .set(RegisterApplication::getStatus,status));
    }
}

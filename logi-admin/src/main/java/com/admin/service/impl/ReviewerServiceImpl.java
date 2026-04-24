package com.admin.service.impl;

import cn.dev33.satoken.stp.StpUtil;
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
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

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

    /**
     * 由 gl-system 注册流程经 Feign 回调写入待审核记录；路由已在 {@link com.security.SaTokenRouteChecks} 中匿名放行，
     * 此处不得再要求登录/审核员权限，否则 Feign 无 token 会失败。
     */
    @Transactional
    public void getRegisterFromSys(RegisterParamDto registerParamDto){
        RegisterApplication app = new RegisterApplication();
        app.setUsername(registerParamDto.getUsername());
        app.setPassword(BCrypt.hashpw(registerParamDto.getPassword(), BCrypt.gensalt()));
        app.setNickname(registerParamDto.getNickname());
        if (StringUtils.hasText(registerParamDto.getUserType())) {
            app.setUserType(Short.parseShort(registerParamDto.getUserType().trim()));
        }
        app.setPhone(registerParamDto.getPhone());
        app.setStatus(registerParamDto.getStatus());
        registerApplicationDao.insert(app);
    }

    @Override
    public CommonResult<?> getOrderReview(int pageNum, int pageSize) {
        StpUtil.checkPermission("reviewer");
        StpUtil.checkLogin();
        return omsServiceClient.getOrderReview(pageNum,pageSize);
    }

    @Override
    public CommonResult<?> accessOrderReview(String orderId, String remark) {
        StpUtil.checkPermission("reviewer");
        StpUtil.checkLogin();
        return omsServiceClient.accessOrderReview(orderId,remark);
    }

    @Override
    public CommonResult<?> rejectOrderReview(String orderId, String remark) {
        StpUtil.checkPermission("reviewer");
        StpUtil.checkLogin();
        return omsServiceClient.rejectOrderReview(orderId,remark);
    }

    public List<RegisterApplication> fetchRegisterApplication(int pageNum,int pageSize){
        StpUtil.checkPermission("reviewer");
        StpUtil.checkLogin();
        IPage<RegisterApplication> page = new Page<>(pageNum, pageSize);
        return registerApplicationDao.selectPage(page,
                new LambdaQueryWrapper<>()).getRecords();
    }

    @Transactional
    public void accessRegister(RegisterParamDto registerParamDto){
        StpUtil.checkPermission("reviewer");
        StpUtil.checkLogin();
        SysUser sysUser = new SysUser();
        BeanUtils.copyProperties(registerParamDto,sysUser);
        sysUser.setUserId(String.valueOf(snowflakeIdGenerator.nextId()));
        redisService.delete(RedisConstant.REGIS_KEY_PREFIX + registerParamDto.getPhone());
        sysUserDao.insert(sysUser);
        confirmReviewed(registerParamDto,(short) 1);
    }
    
    @Transactional
    public void rejectRegister(RegisterParamDto registerParamDto){
        StpUtil.checkPermission("reviewer");
        StpUtil.checkLogin();
        confirmReviewed(registerParamDto,(short) 2);
    }

    public void confirmReviewed(RegisterParamDto registerParamDto,short status){
        StpUtil.checkPermission("reviewer");
        StpUtil.checkLogin();
        registerApplicationDao.update(new LambdaUpdateWrapper<RegisterApplication>()
                .eq(RegisterApplication::getPhone,registerParamDto.getPhone())
                .set(RegisterApplication::getStatus,status));
    }
}

package com.admin.service.impl;

import cn.dev33.satoken.stp.StpUtil;
import com.admin.service.ManagerService;
import com.admin.service.client.OmsServiceClient;
import com.api.CommonResult;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

@Service
public class ManagerServiceImpl implements ManagerService {
    @Resource
    OmsServiceClient omsServiceClient;

    @Override
    public CommonResult<?> getOrder(int pageNum, int pageSize) {
        StpUtil.checkPermission("manager");
        StpUtil.checkLogin();
        return omsServiceClient.getOrder(pageNum,pageSize);
    }

    @Override
    public CommonResult<?> getOrderById(String orderId) {
        StpUtil.checkPermission("manager");
        StpUtil.checkLogin();
        return omsServiceClient.getOrderById(orderId);
    }
}

package com.admin.service.impl;

import cn.dev33.satoken.stp.StpUtil;
import com.admin.dto.SysUserDto;
import com.admin.dto.WmsWarehouseDto;
import com.admin.service.ManagerService;
import com.admin.service.client.ManagerRemoteClient;
import com.api.CommonResult;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

@Service
public class ManagerServiceImpl implements ManagerService {

    @Resource
    private ManagerRemoteClient managerRemoteClient;

    private void checkManager() {
        StpUtil.checkPermission("manager");
        StpUtil.checkLogin();
    }

    @Override
    public CommonResult<?> getOrder(int pageNum, int pageSize) {
        checkManager();
        return managerRemoteClient.getOrder(pageNum, pageSize);
    }

    @Override
    public CommonResult<?> getOrderById(String orderId) {
        checkManager();
        return managerRemoteClient.getOrderById(orderId);
    }

    @Override
    public CommonResult<?> getLine(int pageNum, int pageSize) {
        checkManager();
        return managerRemoteClient.getLine(pageNum, pageSize);
    }

    @Override
    public CommonResult<?> getLineById(Long lineId) {
        checkManager();
        return managerRemoteClient.getLineById(lineId);
    }

    @Override
    public CommonResult<?> getLineDetail(String origin, String dest) {
        checkManager();
        return managerRemoteClient.getLineDetail(origin, dest);
    }

    @Override
    public CommonResult<?> getManualAssignment(int pageNum, int pageSize) {
        checkManager();
        return managerRemoteClient.getManualAssignment(pageNum, pageSize);
    }

    @Override
    public CommonResult<Boolean> manualAssignDriver(String transportOrderId, String driverId) {
        checkManager();
        return managerRemoteClient.manualAssignDriver(transportOrderId, driverId);
    }

    @Override
    public CommonResult<?> getAvailableDriver(int pageNum, int pageSize) {
        checkManager();
        return managerRemoteClient.getAvailableDriver(pageNum, pageSize);
    }

    @Override
    public CommonResult<?> addLine(Long lineId, String origin, String dest, Double estimation, Short status) {
        checkManager();
        return managerRemoteClient.addLine(lineId, origin, dest, estimation, status);
    }

    @Override
    public CommonResult<?> updateLine(Long lineId, String origin, String dest, Double estimation, Short status) {
        checkManager();
        return managerRemoteClient.updateLine(lineId, origin, dest, estimation, status);
    }

    @Override
    public CommonResult<?> getWarehouse(int pageNum, int pageSize) {
        checkManager();
        return managerRemoteClient.getWarehouse(pageNum, pageSize);
    }

    @Override
    public CommonResult<?> addWarehouse(WmsWarehouseDto wmsWarehouseDto) {
        checkManager();
        return managerRemoteClient.addWarehouse(wmsWarehouseDto);
    }

    @Override
    public CommonResult<?> updateWarehouse(WmsWarehouseDto wmsWarehouseDto) {
        checkManager();
        return managerRemoteClient.updateWarehouse(wmsWarehouseDto);
    }

    @Override
    public CommonResult<?> deleteWarehouse(Long warehouseId) {
        checkManager();
        return managerRemoteClient.deleteWarehouse(warehouseId);
    }

    @Override
    public CommonResult<?> fetchSysUserInfo(int pageNum, int pageSize) {
        checkManager();
        return managerRemoteClient.fetchSysUserInfo(pageNum, pageSize);
    }

    @Override
    public CommonResult<?> fetchSysUserByUserType(int pageNum, int pageSize, String userType) {
        checkManager();
        return managerRemoteClient.fetchSysUserByUserType(pageNum, pageSize, userType);
    }

    @Override
    public CommonResult<?> fetchSysUserByUserId(String userId) {
        checkManager();
        return managerRemoteClient.fetchSysUserByUserId(userId);
    }

    @Override
    public CommonResult<?> updateSysUserInfo(SysUserDto sysUserDto) {
        checkManager();
        return managerRemoteClient.updateSysUserInfo(sysUserDto);
    }

    @Override
    public CommonResult<?> deleteSysUser(String userId) {
        checkManager();
        return managerRemoteClient.deleteSysUser(userId);
    }
}

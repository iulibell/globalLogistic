package com.admin.service.client;

import com.admin.dto.SysUserDto;
import com.api.CommonResult;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Component;

/**
 * 管理员域对外部微服务的聚合入口：OMS 订单、TMS 派车、gl-system 用户维护。
 * <p>权限校验在 {@link com.admin.service.impl.ManagerServiceImpl} 中统一处理，本类仅做远程转发。</p>
 */
@Component
public class ManagerRemoteClient {

    @Resource
    private ManagerOmsClient managerOmsClient;
    @Resource
    private TmsServiceClient tmsServiceClient;
    @Resource
    private SystemServiceClient systemServiceClient;

    public CommonResult<?> getOrder(int pageNum, int pageSize) {
        return managerOmsClient.getOrder(pageNum, pageSize);
    }

    public CommonResult<?> getOrderById(String orderId) {
        return managerOmsClient.getOrderById(orderId);
    }

    public CommonResult<?> getLine(int pageNum, int pageSize) {
        return tmsServiceClient.getLine(pageNum, pageSize);
    }

    public CommonResult<?> getLineById(Long lineId) {
        return tmsServiceClient.getLineById(lineId);
    }

    public CommonResult<?> getLineDetail(String origin, String dest) {
        return tmsServiceClient.getLineDetail(origin, dest);
    }

    public CommonResult<?> getManualAssignment(int pageNum, int pageSize) {
        return tmsServiceClient.getManualAssignment(pageNum, pageSize);
    }

    public CommonResult<Boolean> manualAssignDriver(String transportOrderId, String driverId) {
        return tmsServiceClient.manualAssignDriver(transportOrderId, driverId);
    }

    public CommonResult<?> getAvailableDriver(int pageNum, int pageSize) {
        return tmsServiceClient.getAvailableDriver(pageNum, pageSize);
    }

    public CommonResult<?> addLine(Long lineId, String origin, String dest, Double estimation, Short status) {
        return tmsServiceClient.addLine(lineId, origin, dest, estimation, status);
    }

    public CommonResult<?> updateLine(Long lineId, String origin, String dest, Double estimation, Short status) {
        return tmsServiceClient.updateLine(lineId, origin, dest, estimation, status);
    }

    public CommonResult<?> fetchSysUserInfo(int pageNum, int pageSize) {
        return systemServiceClient.fetchSysUserInfo(pageNum, pageSize);
    }

    public CommonResult<?> fetchSysUserByUserType(int pageNum, int pageSize, String userType) {
        return systemServiceClient.fetchSysUserByUserType(pageNum, pageSize, userType);
    }

    public CommonResult<?> fetchSysUserByUserId(String userId) {
        return systemServiceClient.fetchSysUserByUserId(userId);
    }

    public CommonResult<?> updateSysUserInfo(SysUserDto sysUserDto) {
        return systemServiceClient.updateSysUserInfo(sysUserDto);
    }

    public CommonResult<?> deleteSysUser(String userId) {
        return systemServiceClient.deleteSysUser(userId);
    }
}

package com.admin.service;

import com.admin.dto.SysUserDto;
import com.admin.dto.WmsWarehouseDto;
import com.api.CommonResult;

/**
 * 管理员门户业务：订单、运输派车、WMS 仓库、系统用户（经 {@link com.admin.service.client.ManagerRemoteClient} 转发下游）。
 */
public interface ManagerService {

    CommonResult<?> getOrder(int pageNum, int pageSize);

    CommonResult<?> getOrderById(String orderId);

    CommonResult<?> getLine(int pageNum, int pageSize);

    CommonResult<?> getLineById(Long lineId);

    CommonResult<?> getLineDetail(String origin, String dest);

    CommonResult<?> getManualAssignment(int pageNum, int pageSize);

    CommonResult<Boolean> manualAssignDriver(String transportOrderId, String driverId);

    CommonResult<?> getAvailableDriver(int pageNum, int pageSize);

    CommonResult<?> addLine(Long lineId, String origin, String dest, Double estimation, Short status);

    CommonResult<?> updateLine(Long lineId, String origin, String dest, Double estimation, Short status);

    CommonResult<?> getWarehouse(int pageNum, int pageSize);

    CommonResult<?> addWarehouse(WmsWarehouseDto wmsWarehouseDto);

    CommonResult<?> updateWarehouse(WmsWarehouseDto wmsWarehouseDto);

    CommonResult<?> deleteWarehouse(Long warehouseId);

    CommonResult<?> fetchSysUserInfo(int pageNum, int pageSize);

    CommonResult<?> fetchSysUserByUserType(int pageNum, int pageSize, String userType);

    CommonResult<?> fetchSysUserByUserId(String userId);

    CommonResult<?> updateSysUserInfo(SysUserDto sysUserDto);

    CommonResult<?> deleteSysUser(String userId);
}

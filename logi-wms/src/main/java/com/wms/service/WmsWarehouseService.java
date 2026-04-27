package com.wms.service;

import com.wms.dto.WmsWarehouseDto;

import java.util.List;

/**
 * WMS 仓库主数据服务接口。
 */
public interface WmsWarehouseService {
    /**
     * 获取可用仓库信息(电商商家在选址的时候展示)
     * @param pageNum 页数
     * @param pageSize 页大小
     * @return 仓库信息列表
     */
    List<WmsWarehouseDto> getAvailableWarehouse(int pageNum, int pageSize);

    /**
     * 系统间调用：按仓库id查询仓库信息
     * @param warehouseId 仓库id
     * @return 仓库信息，不存在返回null
     */
    WmsWarehouseDto getWarehouseByIdForSys(Long warehouseId);

    /**
     * 获取仓库信息(manager操作)
     * @param pageNum 页数
     * @param pageSize 页大小
     * @return 仓库信息列表
     */
    List<WmsWarehouseDto> getWarehouse(int pageNum, int pageSize);

    /**
     * 添加仓库信息(manager操作)
     * @param wmsWarehouseDto 仓库信息dto
     */
    void addWarehouse(WmsWarehouseDto wmsWarehouseDto);

    /**
     * 更新仓库信息(manager操作)
     * @param wmsWarehouseDto 仓库信息dto
     */
    void updateWarehouse(WmsWarehouseDto wmsWarehouseDto);

    /**
     * 删除仓库(manager操作)
     * @param warehouseId 仓库id
     */
    void deleteWarehouse(Long warehouseId);
}

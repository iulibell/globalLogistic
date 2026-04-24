package com.wms.service;

import com.wms.dto.WmsWarehouseDto;

import java.util.List;

public interface WmsWarehouseService {
    /**
     * 获取可用仓库信息(电商商家在选址的时候展示)
     * @param pageNum 页数
     * @param pageSize 页大小
     * @return 仓库信息列表
     */
    List<WmsWarehouseDto> getAvailableWarehouse(int pageNum, int pageSize);

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

package com.wms.service;

import com.wms.dto.WmsStockDto;

import java.util.List;

/**
 * WMS 库存管理服务接口。
 */
public interface WmsStockService {
    /**
     * 获取库存列表(keeper操作)
     * @param pageNum 页数
     * @param pageSize 页大小
     * @return 库存列表
     */
    List<WmsStockDto> getStock(int pageNum,int pageSize);

    /**
     * 根据库存id获取单个库存商品(keeper操作)
     * @param stockId 库存id
     * @return 单个库存商品
     */
    WmsStockDto getStockById(String stockId);

    /**
     * 手动添加库存(keeper操作)
     * @param wmsStockDto 库存dto
     */
    void addStock(WmsStockDto wmsStockDto);

    /**
     * 更新库存(keeper操作)
     * @param wmsStockDto 库存dto
     */
    void updateStock(WmsStockDto wmsStockDto);

    /**
     * 删除库存(keeper操作)
     * @param stockId 库存id
     */
    void deleteStock(String stockId);
}

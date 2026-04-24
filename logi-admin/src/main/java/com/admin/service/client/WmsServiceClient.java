package com.admin.service.client;

import com.admin.dto.WmsWarehouseDto;
import com.api.CommonResult;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * 管理员对 WMS 的远程调用（仓库分页与增删改）。
 */
@FeignClient(name = "logi-wms", contextId = "adminWmsServiceClient")
public interface WmsServiceClient {

    @GetMapping("/wms/manager/getWarehouse")
    CommonResult<?> getWarehouse(@RequestParam(defaultValue = "1") int pageNum,
                                 @RequestParam(defaultValue = "10") int pageSize);

    @PostMapping("/wms/manager/addWarehouse")
    CommonResult<?> addWarehouse(@RequestBody WmsWarehouseDto wmsWarehouseDto);

    @PostMapping("/wms/manager/updateWarehouse")
    CommonResult<?> updateWarehouse(@RequestBody WmsWarehouseDto wmsWarehouseDto);

    @PostMapping("/wms/manager/deleteWarehouse")
    CommonResult<?> deleteWarehouse(@RequestParam("warehouseId") Long warehouseId);
}

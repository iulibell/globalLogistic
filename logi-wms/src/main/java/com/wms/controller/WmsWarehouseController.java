package com.wms.controller;

import com.api.CommonResult;
import com.wms.dto.WmsWarehouseDto;
import com.wms.service.WmsWarehouseService;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/wms")
public class WmsWarehouseController {
    @Resource
    private WmsWarehouseService wmsWarehouseService;

    @GetMapping("/getAvailableWarehouse")
    public List<WmsWarehouseDto> getAvailableWarehouse(@RequestParam(defaultValue = "1")int pageNum,
                                      @RequestParam(defaultValue = "10")int pageSize){
        return wmsWarehouseService.getAvailableWarehouse(pageNum,pageSize);
    }

    @GetMapping("/sys/getWarehouseById")
    public CommonResult<?> getWarehouseByIdForSys(@RequestParam Long warehouseId){
        return CommonResult.success(wmsWarehouseService.getWarehouseByIdForSys(warehouseId));
    }

    @GetMapping("/manager/getWarehouse")
    public CommonResult<?> getWarehouse(@RequestParam(defaultValue = "1")int pageNum,
                                        @RequestParam(defaultValue = "10")int pageSize){
        return CommonResult.success(wmsWarehouseService.getWarehouse(pageNum, pageSize));
    }

    @PostMapping("/manager/addWarehouse")
    public CommonResult<?> addWarehouse(@RequestBody WmsWarehouseDto wmsWarehouseDto){
        wmsWarehouseService.addWarehouse(wmsWarehouseDto);
        return CommonResult.success("添加成功!");
    }

    @PostMapping("/manager/updateWarehouse")
    public CommonResult<?> updateWarehouse(@RequestBody WmsWarehouseDto wmsWarehouseDto){
        wmsWarehouseService.updateWarehouse(wmsWarehouseDto);
        return CommonResult.success("更新成功!");
    }

    @PostMapping("/manager/deleteWarehouse")
    public CommonResult<?> deleteWarehouse(@RequestParam Long warehouseId){
        wmsWarehouseService.deleteWarehouse(warehouseId);
        return CommonResult.success("已删除该仓库");
    }
}

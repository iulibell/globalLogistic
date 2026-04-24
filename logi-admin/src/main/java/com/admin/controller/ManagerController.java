package com.admin.controller;

import com.admin.dto.WmsWarehouseDto;
import com.admin.service.ManagerService;
import com.api.CommonResult;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;

/**
 * 管理员对外统一入口：原分散在 OMS/TMS 直连与 gl-system /system/admin 的管理员能力均经本控制器聚合。
 */
@RestController
@RequestMapping("/admin/manager")
@Tag(
        name = "ManagerController",
        description = "管理员统一接口：OMS 订单查询、TMS 线路/人工派车、WMS 仓库维护、gl-system 用户分页/条件查询与维护（需 manager 权限）。")
public class ManagerController {
    @Resource
    private ManagerService managerService;

    @GetMapping("/getOrder")
    @Operation(
            summary = "分页获取订单列表",
            description = "转发 logi-oms 分页查询订单主数据。")
    public CommonResult<?> getOrder(@RequestParam(defaultValue = "1") int pageNum,
                                    @RequestParam(defaultValue = "10") int pageSize) {
        return managerService.getOrder(pageNum, pageSize);
    }

    @GetMapping("/getOrderById")
    @Operation(
            summary = "按订单号查询订单",
            description = "根据 orderId 获取单笔订单详情。")
    public CommonResult<?> getOrderById(@RequestParam String orderId) {
        return managerService.getOrderById(orderId);
    }

    @GetMapping("/transport/getLine")
    @Operation(
            summary = "分页查询运输线路",
            description = "转发 TMS，分页返回线路主数据。")
    public CommonResult<?> getLine(@RequestParam(defaultValue = "1") int pageNum,
                                    @RequestParam(defaultValue = "10") int pageSize) {
        return managerService.getLine(pageNum, pageSize);
    }

    @GetMapping("/transport/getLineById")
    @Operation(
            summary = "按线路 ID 查询",
            description = "转发 TMS，根据 lineId 获取单条线路详情。")
    public CommonResult<?> getLineById(@RequestParam Long lineId) {
        return managerService.getLineById(lineId);
    }

    @GetMapping("/transport/getLineDetail")
    @Operation(
            summary = "按起终点查询线路详情",
            description = "转发 TMS，根据 origin + dest 获取线路详情。")
    public CommonResult<?> getLineDetail(@RequestParam String origin,
                                         @RequestParam String dest) {
        return managerService.getLineDetail(origin, dest);
    }

    @GetMapping("/transport/getManualAssignment")
    @Operation(
            summary = "分页待人工派单列表",
            description = "转发 TMS，查看需管理员指定司机的运输单。")
    public CommonResult<?> getManualAssignment(@RequestParam(defaultValue = "1") int pageNum,
                                               @RequestParam(defaultValue = "10") int pageSize) {
        return managerService.getManualAssignment(pageNum, pageSize);
    }

    @PostMapping("/transport/manualAssignDriver")
    @Operation(
            summary = "人工指定司机",
            description = "将运输单指派给指定 driverId；转发 TMS。")
    public CommonResult<Boolean> manualAssignDriver(@RequestParam String transportOrderId,
                                                    @RequestParam String driverId) {
        return managerService.manualAssignDriver(transportOrderId, driverId);
    }

    @GetMapping("/transport/getAvailableDriver")
    @Operation(
            summary = "分页可接单司机列表",
            description = "转发 TMS，返回状态为空闲的司机（用于人工派单时选择）。")
    public CommonResult<?> getAvailableDriver(@RequestParam(defaultValue = "1") int pageNum,
                                              @RequestParam(defaultValue = "10") int pageSize) {
        return managerService.getAvailableDriver(pageNum, pageSize);
    }

    @PostMapping("/transport/addLine")
    @Operation(
            summary = "添加运输线路",
            description = "转发 TMS 新增线路。lineId 字段仅作参数占位，实际由 TMS 生成。")
    public CommonResult<?> addLine(@RequestParam Long lineId,
                                   @RequestParam String origin,
                                   @RequestParam String dest,
                                   @RequestParam Double estimation,
                                   @RequestParam Short status) {
        return managerService.addLine(lineId, origin, dest, estimation, status);
    }

    @PostMapping("/transport/updateLine")
    @Operation(
            summary = "更新运输线路",
            description = "转发 TMS 按业务路线 ID 更新线路信息。")
    public CommonResult<?> updateLine(@RequestParam Long lineId,
                                      @RequestParam String origin,
                                      @RequestParam String dest,
                                      @RequestParam Double estimation,
                                      @RequestParam Short status) {
        return managerService.updateLine(lineId, origin, dest, estimation, status);
    }

    @GetMapping("/wms/getWarehouse")
    @Operation(
            summary = "分页查询仓库",
            description = "转发 logi-wms，分页返回仓库主数据。")
    public CommonResult<?> getWarehouse(@RequestParam(defaultValue = "1") int pageNum,
                                        @RequestParam(defaultValue = "10") int pageSize) {
        return managerService.getWarehouse(pageNum, pageSize);
    }

    @PostMapping("/wms/addWarehouse")
    @Operation(
            summary = "新增仓库",
            description = "转发 logi-wms 创建仓库。")
    public CommonResult<?> addWarehouse(@RequestBody WmsWarehouseDto wmsWarehouseDto) {
        return managerService.addWarehouse(wmsWarehouseDto);
    }

    @PostMapping("/wms/updateWarehouse")
    @Operation(
            summary = "更新仓库",
            description = "转发 logi-wms 更新仓库信息。")
    public CommonResult<?> updateWarehouse(@RequestBody WmsWarehouseDto wmsWarehouseDto) {
        return managerService.updateWarehouse(wmsWarehouseDto);
    }

    @PostMapping("/wms/deleteWarehouse")
    @Operation(
            summary = "删除仓库",
            description = "转发 logi-wms 按 warehouseId 删除仓库。")
    public CommonResult<?> deleteWarehouse(@RequestParam Long warehouseId) {
        return managerService.deleteWarehouse(warehouseId);
    }
}

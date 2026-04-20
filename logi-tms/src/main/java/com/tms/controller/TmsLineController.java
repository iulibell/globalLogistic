package com.tms.controller;

import com.api.CommonResult;
import com.tms.dto.TmsLineDto;
import com.tms.service.TmsLineService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;

/**
 * 运输线路查询；管理员入口由 logi-admin {@code /admin/manager/transport/getLine*} 聚合转发。
 */
@RestController
@RequestMapping("/tms")
@Tag(name = "TmsLineController", description = "运输线路：管理员分页列表与按 ID 查询。")
public class TmsLineController {
    @Resource
    private TmsLineService tmsLineService;

    @GetMapping("/sys/getLineDetail")
    public CommonResult<?> getLineDetail(@RequestParam String origin,
                                         @RequestParam String dest){
        return CommonResult.success(tmsLineService.getLineDetail(origin,dest));
    }

    @GetMapping("/manager/getLine")
    @Operation(summary = "分页查询线路", description = "需 manager 权限；返回线路分页列表。")
    public CommonResult<?> getLine(@RequestParam(defaultValue = "1") int pageNum,
                                   @RequestParam(defaultValue = "10") int pageSize) {
        return CommonResult.success(tmsLineService.getLine(pageNum, pageSize));
    }

    @GetMapping("/manager/getLineById")
    @Operation(summary = "按 ID 查询线路", description = "需 manager 权限；根据主键 lineId 查询单条线路。")
    public CommonResult<?> getLineById(@RequestParam Long lineId) {
        return CommonResult.success(tmsLineService.getLineById(lineId));
    }

    @PostMapping("/manager/addLine")
    public CommonResult<?> addLine(@RequestParam Long lineId,
                                   @RequestParam String origin,
                                   @RequestParam String dest,
                                   @RequestParam Double estimation,
                                   @RequestParam Short status){
        TmsLineDto tmsLineDto = new TmsLineDto();
        tmsLineDto.setLineId(lineId);
        tmsLineDto.setOrigin(origin);
        tmsLineDto.setDest(dest);
        tmsLineDto.setEstimation(estimation);
        tmsLineDto.setStatus(status);
        tmsLineService.addLine(tmsLineDto);
        return CommonResult.success("添加线路成功!");
    }

    @PostMapping("/manager/updateLine")
    public CommonResult<?> updateLine(@RequestParam Long lineId,
                                      @RequestParam String origin,
                                      @RequestParam String dest,
                                      @RequestParam Double estimation,
                                      @RequestParam Short status){
        TmsLineDto tmsLineDto = new TmsLineDto();
        tmsLineDto.setLineId(lineId);
        tmsLineDto.setOrigin(origin);
        tmsLineDto.setDest(dest);
        tmsLineDto.setEstimation(estimation);
        tmsLineDto.setStatus(status);
        tmsLineService.updateLine(tmsLineDto);
        return CommonResult.success("线路修改成功!");
    }
}

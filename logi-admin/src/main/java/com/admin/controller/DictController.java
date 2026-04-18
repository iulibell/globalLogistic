package com.admin.controller;

import com.admin.dto.DictionaryDto;
import com.admin.dto.DictionaryOperationDto;
import com.admin.service.DictionaryService;
import com.api.CommonResult;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin/sys")
@Tag(name = "DictController", description = "字典接口,超管操作,另提供接口供前端获取")
public class DictController {
    @Resource
    DictionaryService dictionaryService;

    @GetMapping("/getDictionary")
    public CommonResult<?> getDictionary(@RequestParam String dictType) {
        return CommonResult.success(dictionaryService.getDictionaryList(dictType));
    }

    @PostMapping("/super/updateDictionary")
    public CommonResult<?> updateDictionary(@RequestBody DictionaryOperationDto dictionaryOperationDto) {
        dictionaryService.updateDictionary(dictionaryOperationDto);
        return CommonResult.success("已更新该字典信息!");
    }

    @PostMapping("/super/addDictionary")
    public CommonResult<?> addDictionary(@RequestBody List<DictionaryDto> dictionaryDtoList) {
        dictionaryService.addDictionary(dictionaryDtoList);
        return CommonResult.success("已成功添加该字典信息!");
    }

    @PostMapping("/super/deleteDictionary")
    public CommonResult<?> deleteDictionary(@RequestBody DictionaryOperationDto dictionaryOperationDto) {
        dictionaryService.deleteDictionary(dictionaryOperationDto);
        return CommonResult.success("已删除该字典!");
    }
}

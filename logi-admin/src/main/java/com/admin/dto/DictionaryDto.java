package com.admin.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class DictionaryDto {
    @NotBlank(message = "字典类型不能为空")
    private String dictType;
    @NotBlank(message = "字典名称不能为空")
    private String dictName;
    @NotBlank(message = "字典值不能为空")
    private String dictValue;
    @NotBlank(message = "请为该字典排序")
    private Integer sort;
    @NotBlank(message = "请完成对应的语言选择")
    private String lang;

    /**
     * 状态：0->禁用,1->启用
     */
    private Short status;
}

package com.admin.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class DictionaryOperationDto {
    @NotBlank(message = "字典类型不能为空")
    private String dictType;
    @NotBlank(message = "字典名字不能为空")
    private String dictName;
    @NotBlank(message = "字典值不能为空")
    private String dictValue;
}

package com.tms.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class TmsLogisticDto {
    @NotBlank(message = "运输单关联id不能为空")
    private String transportOrderId;
    @NotBlank(message = "实时位置不能为空")
    private String city;
    @NotBlank(message = "状态不能为空")
    private Short status;
}

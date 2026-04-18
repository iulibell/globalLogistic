package com.tms.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class TmsLineDto {
    @NotBlank(message = "路线id不能为空")
    private Long lineId;
    @NotBlank(message = "起点不能为空")
    private String origin;
    @NotBlank(message = "终点不能为空")
    private String dest;
    @NotBlank(message = "消耗时间")
    private Double estimation;
    @NotBlank(message = "状态不能为空")
    private Short status;
}

package com.tms.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.util.Date;

@Data
public class TmsLogisticDto {
    @NotBlank(message = "运输单关联id不能为空")
    private String transportOrderId;
    @NotBlank(message = "起点不能为空")
    private String origin;
    @NotBlank(message = "终点不能为空")
    private String dest;
    @NotBlank(message = "实时位置不能为空")
    private String city;
    @NotBlank(message = "状态不能为空")
    private Short status;
    private Date createTime;
    private Date updateTime;
}

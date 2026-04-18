package com.tms.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class TmsVehicleDto {
    @NotBlank(message = "司机id不能为空")
    private String driverId;
    @NotBlank(message = "车牌号不能为空")
    private String vehicleNo;
    @NotBlank(message = "体高不能为空")
    private BigDecimal height;
    @NotBlank(message = "体长不能为空")
    private BigDecimal length;
    @NotBlank(message = "体宽不能为空")
    private BigDecimal wide;
    @NotBlank(message = "吨位不能为空")
    private BigDecimal tonnage;
}

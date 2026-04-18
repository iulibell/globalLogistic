package com.tms.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class TmsVehicleDto {
    @NotBlank(message = "司机id不能为空")
    private String driverId;
    @NotBlank(message = "车牌号不能为空")
    private String vehicleNo;
    @NotNull(message = "体高不能为空")
    private BigDecimal height;
    @NotNull(message = "体长不能为空")
    private BigDecimal length;
    @NotNull(message = "体宽不能为空")
    private BigDecimal wide;
    @NotNull(message = "吨位不能为空")
    private BigDecimal tonnage;
}

package com.tms.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class TmsDriverDto {
    @NotBlank(message = "司机id不能为空")
    private String userId;
    @NotBlank(message = "车牌号不能为空")
    private String vehicleNo;
    @NotBlank(message = "所在城市不能为空")
    private String currentCity;
}

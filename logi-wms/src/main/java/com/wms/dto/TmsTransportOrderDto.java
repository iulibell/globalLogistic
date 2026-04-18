package com.wms.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class TmsTransportOrderDto {
    @NotBlank(message = "运输订单id")
    private String transportOrderId;
    @NotBlank(message = "起点不能为空")
    private String origin;
    @NotBlank(message = "商家/收货用户城市不能为空")
    private String dest;
    @NotBlank(message = "运输费用不能为空")
    private BigDecimal fee;
}

package com.oms.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class TmsTransportOrderDto {
    @NotBlank(message = "tms_v_transport_order_id")
    private String transportOrderId;
    private String phone;
    @NotBlank(message = "tms_v_origin")
    private String origin;
    @NotBlank(message = "tms_v_dest")
    private String dest;
    @NotBlank(message = "tms_v_fee")
    private BigDecimal fee;
}

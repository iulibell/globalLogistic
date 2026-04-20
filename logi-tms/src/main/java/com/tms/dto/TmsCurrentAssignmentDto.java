package com.tms.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class TmsCurrentAssignmentDto {
    private String transportOrderId;
    private String orderId;
    private String origin;
    private String dest;
    private String phone;
    private BigDecimal fee;
    /**
     * 1->已接单,2->运输中,3->已送达
     */
    private Short status;
}


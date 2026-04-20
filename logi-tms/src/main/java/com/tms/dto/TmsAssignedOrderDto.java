package com.tms.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class TmsAssignedOrderDto {
    private String transportId;
    private String origin;
    private String driverId;
    private String dest;
    private BigDecimal fee;
    /**
     * 0->待接受,1->已拒绝,2->已接受
     */
    private Short status;
}


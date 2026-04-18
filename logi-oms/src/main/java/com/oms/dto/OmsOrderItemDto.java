package com.oms.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class OmsOrderItemDto {
    @NotBlank(message = "订单id不能为空")
    private String orderId;
    @NotBlank(message = "商品名称不能为空")
    private String skuName;
    @NotBlank(message = "商品编号不能为空")
    private String skuCode;
    @NotBlank(message = "商品单价不能为空")
    private BigDecimal price;
    @NotBlank(message = "下订数量不能为空")
    private Integer quantity;
    @NotBlank(message = "下单总价不能为空")
    private BigDecimal totalPrice;
}

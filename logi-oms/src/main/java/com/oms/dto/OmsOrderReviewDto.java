package com.oms.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class OmsOrderReviewDto {
    @NotBlank(message = "商品id不能为空")
    private String orderId;
    @NotBlank(message = "商品名称不能为空")
    private String skuName;
    @NotBlank(message = "商品编号不能为空")
    private String skuCode;
    @NotBlank(message = "商品类型不能为空")
    private String type;
    @NotBlank(message = "购买数量不能为空")
    private Integer quantity;
    @NotBlank(message = "审核状态不能为空")
    private Short status;
    private String remark;
}

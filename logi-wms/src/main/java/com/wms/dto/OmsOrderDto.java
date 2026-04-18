package com.wms.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class OmsOrderDto {
    @NotBlank(message = "订单id不能为空")
    private String orderId;
    @NotBlank(message = "商家id不能为空")
    private String merchantId;
    @NotBlank(message = "用户id不能为空")
    private String userId;
    @NotBlank(message = "商品id不能为空")
    private String goodsId;
    @NotBlank(message = "仓库id不能为空")
    private Long warehouseId;
    @NotBlank(message = "库位id不能为空")
    private Long locationId;
    @NotBlank(message = "商品名称不能为空")
    private String skuName;
    @NotBlank(message = "商品编号不能为空")
    private String skuCode;
    @NotBlank(message = "支付金额不能为空")
    private BigDecimal price;
    @NotBlank(message = "用户手机号不能为空")
    private String userPhone;
    @NotBlank(message = "商家手机号不能为空")
    private String merchantPhone;
    @NotBlank(message = "仓库所属城市不能为空")
    private String warehouseCity;
    @NotBlank(message = "用户城市不能为空")
    private String city;
    @NotBlank(message = "种类不能为空")
    private Short category;
    @NotBlank(message = "购买数量不能为空")
    private Integer quantity;
    @NotBlank(message = "商品类型不能为空")
    private String type;
    private String remark;
}

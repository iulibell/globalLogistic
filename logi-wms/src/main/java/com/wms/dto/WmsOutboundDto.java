package com.wms.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class WmsOutboundDto {
    @NotBlank(message = "出库id不能为空")
    private String outboundId;
    @NotBlank(message = "仓库id不能为空")
    private Long warehouseId;
    @NotBlank(message = "库存id不能为空")
    private String stockId;
    @NotBlank(message = "关联订单id不能为空")
    private String orderId;
    @NotBlank(message = "库位id不能为空")
    private Long locationId;
    @NotBlank(message = "商品名称不能为空")
    private String skuName;
    @NotBlank(message = "商品编号不能为空")
    private String skuCode;
    @NotBlank(message = "用户手机号不能为空")
    private String userPhone;
    @NotBlank(message = "商家手机号不能为空")
    private String merchantPhone;
    @NotBlank(message = "用户城市不能为空")
    private String city;
    private Short status;
}

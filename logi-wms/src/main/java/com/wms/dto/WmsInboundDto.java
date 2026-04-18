package com.wms.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class WmsInboundDto {
    @NotBlank(message = "入库关联id不能为空")
    private String inboundId;
    @NotBlank(message = "仓库id不能为空")
    private Long warehouseId;
    @NotBlank(message = "库位id不能为空")
    private Long locationId;
    @NotBlank(message = "商家id不能为空")
    private String merchantId;
    @NotBlank(message = "商品名称不能为空")
    private String skuName;
    @NotBlank(message = "商品编号不能为空")
    private String skuCode;
    @NotBlank(message = "商家手机号不能为空")
    private String merchantPhone;
    private String merchantRemark;
    private Short status;
}

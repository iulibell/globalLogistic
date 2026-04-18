package com.wms.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class WmsInboundItemDto {
    @NotBlank(message = "入库关联id不能为空")
    private String inboundId;
    @NotBlank(message = "商品名称不能为空")
    private String skuName;
    @NotBlank(message = "商品编号不能为空")
    private String skuCode;
    @NotBlank(message = "入库数量不能为空")
    private Integer quantity;
}


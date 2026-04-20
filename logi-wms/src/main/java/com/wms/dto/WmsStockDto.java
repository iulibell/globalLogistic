package com.wms.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class WmsStockDto {
    @NotBlank(message = "库存id不能为空")
    private String stockId;
    @NotNull(message = "库位id不能为空")
    private Long locationId;
    @NotNull(message = "仓库id不能为空")
    private Long warehouseId;
    @NotBlank(message = "商品名称不能为空")
    private String skuName;
    @NotBlank(message = "库存编号不能为空")
    private String skuCode;
    private Integer stockQuantity;
}

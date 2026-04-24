package com.wms.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class WmsOutboundDto {
    @NotBlank(message = "wms_v_outbound_id")
    private String outboundId;
    @NotNull(message = "wms_v_warehouse_id")
    private Long warehouseId;
    @NotBlank(message = "wms_v_stock_id")
    private String stockId;
    @NotBlank(message = "wms_v_order_id")
    private String orderId;
    @NotNull(message = "wms_v_location_id")
    private Long locationId;
    @NotBlank(message = "wms_v_sku_name")
    private String skuName;
    @NotBlank(message = "wms_v_sku_code")
    private String skuCode;
    @NotBlank(message = "wms_v_user_phone")
    private String userPhone;
    @NotBlank(message = "wms_v_merchant_phone")
    private String merchantPhone;
    @NotBlank(message = "wms_v_city")
    private String city;
    private Short status;
}

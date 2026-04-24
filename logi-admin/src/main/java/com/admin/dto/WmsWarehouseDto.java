package com.admin.dto;

import lombok.Data;

/**
 * 与 logi-wms {@code WmsWarehouseDto} 字段对齐，供 Feign 请求体序列化（避免 logi-admin 依赖 logi-wms 模块）。
 */
@Data
public class WmsWarehouseDto {
    private Long warehouseId;
    private String city;
    private String country;
    private String address;
    private Short status;
}

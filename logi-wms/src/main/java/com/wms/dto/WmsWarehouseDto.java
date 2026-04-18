package com.wms.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class WmsWarehouseDto {
    @NotBlank(message = "仓库id不能为空")
    private Long warehouseId;
    @NotBlank(message = "所属城市不能为空")
    private String city;
    @NotBlank(message = "所属国家不能为空")
    private String country;
    @NotBlank(message = "具体地址不能为空")
    private String address;
    @NotBlank(message = "状态不能为空")
    private Short status;
}

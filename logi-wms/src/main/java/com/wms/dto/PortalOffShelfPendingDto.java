package com.wms.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 与 mall-portal {@code PortalOffShelfSysDto} JSON 字段一致，供 Feign 反序列化。
 */
@Data
public class PortalOffShelfPendingDto {
    private Long id;
    private String goodsId;
    private String merchantId;
    private String city;
    private BigDecimal fee;
    private Short status;
    private Date createTime;
    private Date updateTime;
    private String skuName;
}

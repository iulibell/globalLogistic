package com.wms.dto;

import lombok.Data;

import java.math.BigDecimal;

/**
 * 与 mall-portal {@link com.portal.dto.PortalGoodsDto} JSON 字段一致，供 Feign 反序列化。
 */
@Data
public class PortalGoodsRemoteDto {
    private String merchantId;
    private String goodsId;
    private String skuName;
    private String picture;
    private Short category;
    private BigDecimal price;
    private String type;
    private String description;
}

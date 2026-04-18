package com.wms.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.Date;

@Data
@TableName("wms_stock_lock")
@Schema(description = "库存锁实体")
public class WmsStockLock {
    @TableId(type = IdType.AUTO)
    private Long id;
    @Schema(description = "仓库id")
    private Long warehouseId;
    @Size(max = 255)
    @Schema(description = "关联orderId")
    private String orderId;
    @Size(max = 255)
    @Schema(description = "库存id(与电商中的商品id对应)")
    private String stockId;
    @Schema(description = "库位id")
    private Long locationId;
    @Size(max = 50)
    @Schema(description = "商品名称")
    private String skuName;
    @Size(max = 50)
    @Schema(description = "商品库存编号")
    private String skuCode;
    @Size(max = 11)
    @Schema(description = "用户手机号")
    private String userPhone;
    @Size(max = 11)
    @Schema(description = "商家手机号")
    private String merchantPhone;
    @Size(max = 20)
    @Schema(description = "用户城市")
    private String city;
    @Schema(description = "锁库数量")
    private Integer lockQuantity;
    @Schema(description = "状态:0->已解锁,1->上锁中(默认)")
    private Short status;
    private Date createTime;
    private Date updateTime;
}

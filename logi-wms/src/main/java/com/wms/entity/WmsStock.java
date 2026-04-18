package com.wms.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.Date;

@Data
@TableName("wms_stock")
@Schema(description = "库存实体")
public class WmsStock {
    @TableId(type = IdType.AUTO)
    private Long id;
    @Size(max = 255)
    @Schema(description = "库存id(与电商中的商品id对应)")
    private String stockId;
    @Size(max = 50)
    @Schema(description = "库存所属的仓库id")
    private Long warehouseId;
    @Schema(description = "库位id")
    private Long locationId;
    @Size(max = 50)
    @Schema(description = "商品名称")
    private String skuName;
    @Size(max = 50)
    @Schema(description = "商品库存编号")
    private String skuCode;
    @Schema(description = "库存数量")
    private Integer stockQuantity;
    @Schema(description = "锁库数量")
    private Integer lockQuantity;
    private Date createTime;
    private Date updateTime;
}

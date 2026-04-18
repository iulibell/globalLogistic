package com.wms.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.Date;

@Data
@TableName("wms_inbound")
public class WmsInbound {
    @TableId(type = IdType.AUTO)
    private Long id;
    @Schema(description = "仓库id")
    private Long warehouseId;
    @Schema(description = "库位id,入库分配")
    private Long locationId;
    @Size(max = 255)
    @Schema(description = "入库关联id")
    private String inboundId;
    @Size(max = 255)
    @Schema(description = "商家id")
    private String merchantId;
    @Schema(description = "入存数量")
    private Integer quantity;
    @Size(max = 11)
    @Schema(description = "商家手机号")
    private String merchantPhone;
    @Size(max = 50)
    @Schema(description = "商品名称")
    private String skuName;
    @Size(max = 50)
    @Schema(description = "商品库存编号")
    private String skuCode;
    @Size(max = 500)
    @Schema(description = "商家备注")
    private String merchantRemark;
    @Schema(description = "状态:0->待入库,1->已入库")
    private Short status;
    private Date createTime;
    private Date updateTime;
}
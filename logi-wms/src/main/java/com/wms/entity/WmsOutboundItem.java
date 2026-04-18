package com.wms.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.Date;

@Data
@TableName("wms_outbound_item")
public class WmsOutboundItem {
    @TableId(type = IdType.AUTO)
    private Long id;
    @Size(max = 255)
    @Schema(description = "出库关联id")
    private String outboundId;
    @Size(max = 50)
    @Schema(description = "商品名称")
    private String skuName;
    @Size(max = 50)
    @Schema(description = "商品编号")
    private String skuCode;
    @Schema(description = "出库数量")
    private Integer quantity;
    private Date createTime;
    private Date updateTime;
}

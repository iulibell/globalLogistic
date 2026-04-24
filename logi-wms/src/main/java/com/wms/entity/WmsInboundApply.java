package com.wms.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

@Data
@TableName("wms_inbound_apply")
public class WmsInboundApply {
    @TableId(type = IdType.AUTO)
    private Long id;
    @Size(max = 255)
    @Schema(description = "商品id")
    private String goodsId;
    @Schema(description = "申请入库的仓库id")
    private Long warehouseId;
    @Size(max = 255)
    @Schema(description = "申请入库id")
    private String applyId;
    @Size(max = 255)
    @Schema(description = "商家id")
    private String merchantId;
    @Size(max = 11)
    @Schema(description = "商家手机号")
    private String merchantPhone;
    @Size(max = 50)
    @Schema(description = "商品名称")
    private String skuName;
    @Size(max = 20)
    @Schema(description = "申请仓库所属城市")
    private String warehouseCity;
    @Size(max = 20)
    @Schema(description = "商家所在城市")
    private String city;
    @Schema(description = "申请数量")
    private Integer applyQuantity;
    @Schema(description = "状态:0->待审核,1->未通过审核,2->待支付,3->超时未支付,4->已支付")
    private Short status;
    @Size(max = 500)
    @Schema(description = "商家备注")
    private String merchantRemark;
    @Size(max = 500)
    @Schema(description = "备注，未通过审核时添加")
    private String rejectRemark;
    @Size(max = 10)
    @Schema(description = "入库申请支付金额(通过审核后设置,含运费)")
    private BigDecimal fee;
    private Date createTime;
    private Date updateTime;
}

package com.oms.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.Date;

@Data
@TableName("oms_order_review")
@Schema(description = "订单审核实体,对特殊订单(category->1)进行审核")
public class OmsOrderReview {
    @TableId(type = IdType.AUTO)
    private Long id;
    @Size(max = 255)
    @Schema(description = "订单id")
    private String orderId;
    @Size(max = 50)
    @Schema(description = "商品名称")
    private String skuName;
    @Size(max = 50)
    @Schema(description = "商品编号")
    private String skuCode;
    @Size(max = 20)
    @Schema(description = "商品类型")
    private String type;
    @Schema(description = "审核状态:0->待审核,1->已审核")
    private Short status;
    private Date createTime;
    private Date updateTime;
}

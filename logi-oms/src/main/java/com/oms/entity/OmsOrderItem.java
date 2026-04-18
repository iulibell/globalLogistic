package com.oms.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

@Data
@TableName("oms_order_item")
@Schema(description = "订单项实体")
public class OmsOrderItem {
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
    @Size(max = 10)
    @Schema(description = "商品单价")
    private BigDecimal price;
    @Schema(description = "下订数量")
    private Integer quantity;
    @Size(max = 10)
    @Schema(description = "下单总价")
    private BigDecimal totalPrice;
    private Date createTime;
}

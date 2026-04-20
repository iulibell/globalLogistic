package com.tms.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

@Data
@TableName("tms_assigned_order")
public class TmsAssignedOrder {
    @TableId(type = IdType.AUTO)
    private Long id;
    @Size(max = 255)
    @Schema(description = "指定派单的运输单号")
    private String transportId;
    @Size(max = 20)
    @Schema(description = "起点")
    private String origin;
    @Size(max = 255)
    @Schema(description = "司机id")
    private String driverId;
    @Size(max = 20)
    @Schema(description = "终点")
    private String dest;
    @Size(max = 10)
    @Schema(description = "运费")
    private BigDecimal fee;
    @Schema(description = "状态:0->待接受,1->已拒绝")
    private Short status;
    private Date createTime;
    private Date updateTime;
}

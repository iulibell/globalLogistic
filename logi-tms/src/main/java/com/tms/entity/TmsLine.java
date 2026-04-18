package com.tms.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
@TableName("tms_line")
@Schema(description = "路线实体")
public class TmsLine {
    @TableId(type = IdType.AUTO)
    private Long id;
    @Size(max = 255)
    @Schema(description = "路线id")
    private Long lineId;
    @Size(max = 20)
    @Schema(description = "起点")
    private String origin;
    @Size(max = 20)
    @Schema(description = "终点")
    private String dest;
    @Schema(description = "状态:0->禁用,1->启用(默认)")
    private Short status;
    @Size(max = 10)
    @Schema(description = "消耗时间")
    private Double estimation;
}

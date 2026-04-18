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
@TableName("tms_vehicle")
@Schema(description = "车辆实体类")
public class TmsVehicle {
    @TableId(type = IdType.AUTO)
    private Long id;
    @Size(max = 255)
    @Schema(description = "司机id")
    private String driverId;
    @Size(max = 20)
    @Schema(description = "车牌号")
    private String vehicleNo;
    @Size(max = 10)
    @Schema(description = "体高")
    private BigDecimal height;
    @Size(max = 10)
    @Schema(description = "体长")
    private BigDecimal length;
    @Size(max = 10)
    @Schema(description = "体宽")
    private BigDecimal wide;
    @Size(max = 10)
    @Schema(description = "吨位")
    private BigDecimal tonnage;
    private Date createTime;
    private Date updateTime;
}

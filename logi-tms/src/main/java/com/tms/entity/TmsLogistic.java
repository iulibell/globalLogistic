package com.tms.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.Date;

@Data
@TableName("tms_logistic")
@Schema(description = "物流轨迹实体")
public class TmsLogistic {
    @TableId(type = IdType.AUTO)
    private Long id;
    @Size(max = 255)
    @Schema(description = "运输订单关联id")
    private String transportOrderId;
    @Size(max = 255)
    @Schema(description = "司机id")
    private String driverId;
    @Size(max = 20)
    @Schema(description = "实时位置(目前需司机手动输入,后期可集成高德API)")
    private String city;
    @Schema(description = "物流状态:1->运输中(默认),2->已送达")
    private Short status;
    private Date createTime;
    private Date updateTime;
}

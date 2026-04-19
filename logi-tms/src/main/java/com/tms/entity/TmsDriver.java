package com.tms.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.Date;

@Data
@TableName("tms_driver")
@Schema(description = "运输司机实体")
public class TmsDriver {
    @TableId(type = IdType.AUTO)
    private Long id;
    @Size(max = 255)
    @Schema(description = "与sysUser中的userId进行关联")
    private String userId;
    @Size(max = 20)
    @Schema(description = "车牌号")
    private String vehicleNo;
    @Size(max = 20)
    @Schema(description = "所在城市")
    private String currentCity;
    @Schema(description = "权重,默认值为100")
    private Integer weight;
    @Schema(description = "状态:0->空闲,1->运输中,2->休息中,3->离职")
    private Short status;
    private Date createTime;
    private Date updateTime;
}

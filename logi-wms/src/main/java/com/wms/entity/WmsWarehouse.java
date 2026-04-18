package com.wms.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.Date;

@Data
@TableName("wms_warehouse")
@Schema(description = "仓库实体")
public class WmsWarehouse {
    @TableId(type = IdType.AUTO)
    private Long warehouseId;
    @Size(max = 20)
    @Schema(description = "所属城市")
    private String city;
    @Size(max = 20)
    @Schema(description = "所属国家")
    private String country;
    @Size(max = 100)
    @Schema(description = "具体地址")
    private String address;
    @Schema(description = "状态:0->禁用,1->启用")
    private Short status;
    private Date createTime;
    private Date updateTime;
}

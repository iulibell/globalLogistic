package com.wms.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.Date;

@Data
@TableName("wms_outbound")
@Schema(description = "出库实体")
public class WmsOutbound {
    @TableId(type = IdType.AUTO)
    private Long id;
    @Size(max = 255)
    @Schema(description = "出库id")
    private String outboundId;
    @Size(max = 255)
    @Schema(description = "关联订单id")
    private String orderId;
    @Size(max = 255)
    @Schema(description = "库存id")
    private String stockId;
    @Size(max = 50)
    @Schema(description = "库存所属的仓库id")
    private Long warehouseId;
    @Schema(description = "库位id")
    private Long locationId;
    @Size(max = 50)
    @Schema(description = "商品名称")
    private String skuName;
    @Size(max = 50)
    @Schema(description = "商品库存编号")
    private String skuCode;
    @Size(max = 11)
    @Schema(description = "用户手机号")
    private String userPhone;
    @Size(max = 11)
    @Schema(description = "商家手机号")
    private String merchantPhone;
    @Size(max = 30)
    @Schema(description = "用户城市")
    private String city;
    @Schema(description = "状态:0->待出库(默认),1->已出库")
    private Short status;
    private Date createTime;
    private Date updateTime;
}

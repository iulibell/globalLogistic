package com.tms.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.validator.ParamValidator;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

@Data
@TableName("tms_transport_order")
@Schema(description = "运输单实体")
public class TmsTransportOrder {
    @TableId(type = IdType.AUTO)
    private Long id;
    @Size(max = 255)
    @Schema(description = "商品id")
    private String goodsId;
    @Size(max = 255)
    @Schema(description = "运输订单id")
    private String transportOrderId;
    @Size(max = 255)
    @Schema(description = "关联order订单id")
    private String orderId;
    @Schema(description = "仓库id")
    private Long warehouseId;
    @Size(max = 255)
    @Schema(description = "司机id")
    private String driverId;
    @Size(max = 255)
    @Schema(description = "最近一次拒单司机用户id(含人工指派后拒单)")
    private String lastRejectDriverId;
    @Size(max = 20)
    @Schema(description = "起点不能为空")
    private String origin;
    @Size(max = 11)
    @Schema(description = "商家/收货用户电话")
    private String phone;
    @Size(max = 20)
    @Schema(description = "终点不能为空")
    private String dest;
    @Schema(description = "状态:0->待接单,1->已接单,2->运输中,3->已送达,4->已签收,5->派单超时(无人接单),6->待人工分配司机(拒单满4次)")
    @ParamValidator(value = {"0","1","2","3","4","5","6"},message = "运输状态错误")
    private Short status;
    @Size(max = 10)
    @Schema(description = "运输费用")
    private BigDecimal fee;
    @Schema(description = "拒绝次数")
    private Integer rejectCount;
    private Date createTime;
    private Date updateTime;
}

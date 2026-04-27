package com.oms.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.oms.entity.OmsOrder;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.Date;

public interface OmsOrderDao extends BaseMapper<OmsOrder> {

    @Select("SELECT COALESCE(SUM(i.quantity), 0) FROM oms_order o INNER JOIN oms_order_item i ON o.order_id = i.order_id "
            + "WHERE o.user_id = #{userId} AND o.goods_id = #{goodsId} AND o.status IN (3, 5) "
            + "AND o.create_time >= #{startTime} AND o.create_time <= #{endTime}")
    Integer sumItemQuantityForUserGoodsBetween(@Param("userId") String userId,
                                               @Param("goodsId") String goodsId,
                                               @Param("startTime") Date startTime,
                                               @Param("endTime") Date endTime);
}

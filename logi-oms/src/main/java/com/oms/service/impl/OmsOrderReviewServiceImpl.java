package com.oms.service.impl;

import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.core.bean.BeanUtil;
import cn.ipokerface.snowflake.SnowflakeIdGenerator;
import io.seata.spring.annotation.GlobalTransactional;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.constant.RabbitConstant;
import com.constant.RedisConstant;
import com.oms.dao.OmsOrderDao;
import com.oms.dao.OmsOrderReviewDao;
import com.oms.dto.OmsOrderDto;
import com.oms.dto.OmsOrderReviewDto;
import com.oms.entity.OmsOrder;
import com.oms.entity.OmsOrderReview;
import com.oms.service.OmsOrderReviewService;
import com.oms.service.client.WmsServiceClient;
import com.service.RedisService;
import jakarta.annotation.Resource;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.concurrent.TimeUnit;

@Service
public class OmsOrderReviewServiceImpl implements OmsOrderReviewService {
    @Resource
    private OmsOrderReviewDao omsOrderReviewDao;
    @Resource
    private OmsOrderDao omsOrderDao;
    @Resource
    private RedisService redisService;
    @Resource
    private WmsServiceClient wmsServiceClient;
    @Resource
    private RabbitTemplate rabbitTemplate;
    @Resource
    private SnowflakeIdGenerator snowflakeIdGenerator;

    @Override
    @Transactional
    public void addOrderView(OmsOrderDto omsOrderDto) {
        OmsOrderReview omsOrderReview = new OmsOrderReview();
        OmsOrder omsOrder = new OmsOrder();
        BeanUtil.copyProperties(omsOrderDto,omsOrder);
        BeanUtil.copyProperties(omsOrderDto,omsOrderReview);
        omsOrderDao.insert(omsOrder);
        omsOrderReviewDao.insert(omsOrderReview);
    }

    @Override
    public List<OmsOrderReviewDto> getOrderReview(int pageNum, int pageSize) {
        StpUtil.checkPermission("reviewer");
        StpUtil.checkLogin();
        IPage<OmsOrderReview> page = new Page<>(pageNum,pageSize);
        omsOrderReviewDao.selectPage(page,null);
        return page.convert(omsOrderReview -> {
            OmsOrderReviewDto omsOrderReviewDto = new OmsOrderReviewDto();
            BeanUtil.copyProperties(omsOrderReview,omsOrderReviewDto);
            return omsOrderReviewDto;
        }).getRecords();
    }

    @Override
    @Transactional
    @GlobalTransactional(name = "oms-access-order-review", rollbackFor = Exception.class)
    public void accessOrderReview(String orderId,String remark) {
        StpUtil.checkPermission("reviewer");
        StpUtil.checkLogin();
        updateOrderStatus(orderId,(short)2,remark);

        OmsOrderDto omsOrderDto = new OmsOrderDto();
        BeanUtils.copyProperties(omsOrderDao.selectOne(new LambdaQueryWrapper<OmsOrder>()
                .eq(OmsOrder::getOrderId,orderId)),omsOrderDto);
        wmsServiceClient.stockLock(omsOrderDto);

        redisService.set(RedisConstant.ORDER_KEY_PREFIX + orderId,
                "",
                RedisConstant.ORDER_EXPIRE_TIME,
                TimeUnit.MINUTES);
        rabbitTemplate.convertAndSend(RabbitConstant.ORDER_TTL_EXCHANGE,
                RabbitConstant.ORDER_TTL_ROUTING_KEY,
                orderId);
    }

    @Override
    @Transactional
    @GlobalTransactional(name = "oms-reject-order-review", rollbackFor = Exception.class)
    public void rejectOrderReview(String orderId,String remark) {
        StpUtil.checkPermission("reviewer");
        StpUtil.checkLogin();
        updateOrderStatus(orderId,(short)1,remark);
        wmsServiceClient.stockUnlock(orderId);
    }

    private void updateOrderStatus(String orderId,Short orderStatus,String remark){
        StpUtil.checkPermission("reviewer");
        StpUtil.checkLogin();
        omsOrderReviewDao.update(new LambdaUpdateWrapper<OmsOrderReview>()
                .eq(OmsOrderReview::getOrderId,orderId)
                .set(OmsOrderReview::getStatus, (short)1));
        omsOrderDao.update(new LambdaUpdateWrapper<OmsOrder>()
                .eq(OmsOrder::getOrderId,orderId)
                .set(OmsOrder::getStatus, orderStatus)
                .set(OmsOrder::getRemark,remark));
    }
}

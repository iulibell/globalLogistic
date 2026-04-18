package com.oms.schedule;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.oms.dao.OmsOrderReviewDao;
import com.oms.entity.OmsOrderReview;
import jakarta.annotation.Resource;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class ReviewedSchedule {

    @Resource
    private OmsOrderReviewDao omsOrderReviewDao;
    /**
     * 清理消费成功的消息日志,每小时进行清理
     */
    @Scheduled(cron = "0 0 * * * ?")
    @Async
    public void cleanReviewedApplication() {
        omsOrderReviewDao.delete(new LambdaQueryWrapper<OmsOrderReview>()
                .eq(OmsOrderReview::getStatus,(short)1));
    }
}

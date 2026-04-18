package com.message.consumer;

import com.api.CommonResult;
import com.api.ResultCode;
import com.constant.RabbitConstant;
import com.exception.Assert;
import com.message.service.MqMessageLogService;
import com.message.service.client.OmsServiceClient;
import com.rabbitmq.client.Channel;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@Slf4j
public class OmsOrderConsumer {

    @Resource
    private MqMessageLogService mqMessageLogService;
    @Resource
    private OmsServiceClient omsServiceClient;

    @RabbitListener(queues = RabbitConstant.ORDER_DEAD_QUEUE)
    public void orderDeadQueue(Channel channel, Message mqMessage, String orderId) throws IOException {
        if(orderId == null)
            Assert.fail("订单死信队列未接收到orderId");
        long deliveryTag = mqMessage.getMessageProperties().getDeliveryTag();
        try{
            CommonResult<Boolean> result = omsServiceClient.markOrderPaymentTimeout(orderId);
            if (result != null
                    && result.getCode() == ResultCode.SUCCESS.getCode()
                    && Boolean.TRUE.equals(result.getData())) {
                mqMessageLogService.saveOrderTimeoutLog(orderId);
            }
            channel.basicAck(deliveryTag, false);
        }catch (Exception e){
            log.info("订单死信队列出现异常" + e.getMessage());
            mqMessageLogService.saveOrderTimeoutFailedLog(orderId, e.getMessage());
            channel.basicNack(deliveryTag, false, false);
        }
    }
}

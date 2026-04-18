package com.message.consumer;

import com.api.CommonResult;
import com.api.ResultCode;
import com.constant.RabbitConstant;
import com.exception.Assert;
import com.message.service.MqMessageLogService;
import com.message.service.client.WmsServiceClient;
import com.rabbitmq.client.Channel;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@Slf4j
public class WmsInboundConsumer {
    @Resource
    private MqMessageLogService mqMessageLogService;
    @Resource
    private WmsServiceClient wmsServiceClient;

    @RabbitListener(queues = RabbitConstant.INBOUND_DEAD_QUEUE)
    public void inboundDeadQueue(Channel channel, Message mqMessage, String applyId) throws IOException {
        if (applyId == null)
            Assert.fail("申请入库死信队列未接收到applyId");
        long deliveryTag = mqMessage.getMessageProperties().getDeliveryTag();
        try{
            CommonResult<Boolean> result = wmsServiceClient.markInboundApplyPaymentTimeout(applyId);
            if (result != null
                    && result.getCode() == ResultCode.SUCCESS.getCode()
                    && Boolean.TRUE.equals(result.getData())) {
                mqMessageLogService.saveInboundTimeoutLog(applyId);
            }
            channel.basicAck(deliveryTag, false);
        }catch (Exception e){
            log.info("申请入库死信队列发生异常" + e.getMessage());
            mqMessageLogService.saveInboundTimeoutFailedLog(applyId, e.getMessage());
            channel.basicNack(deliveryTag, false, false);
        }
    }
}

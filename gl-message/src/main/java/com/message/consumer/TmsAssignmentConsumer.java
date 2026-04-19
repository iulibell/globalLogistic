package com.message.consumer;

import com.api.CommonResult;
import com.api.ResultCode;
import com.constant.RabbitConstant;
import com.exception.Assert;
import com.message.service.MqMessageLogService;
import com.message.service.client.TmsServiceClient;
import com.rabbitmq.client.Channel;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@Slf4j
public class TmsAssignmentConsumer {
    @Resource
    private MqMessageLogService mqMessageLogService;
    @Resource
    private TmsServiceClient tmsServiceClient;

    @RabbitListener(queues = RabbitConstant.ASSIGN_DEAD_QUEUE)
    public void assignDeadQueue(Channel channel, Message mqMessage, String transportOrderId) throws IOException {
        if (transportOrderId == null)
            Assert.fail("mq_dlq_tms_transport_order_id");
        long deliveryTag = mqMessage.getMessageProperties().getDeliveryTag();
        try{
            CommonResult<Boolean> result = tmsServiceClient.handleAssignWindowExpired(transportOrderId);
            if (result != null
                    && result.getCode() == ResultCode.SUCCESS.getCode()
                    && Boolean.TRUE.equals(result.getData())) {
                mqMessageLogService.saveAssignTimeoutLog(transportOrderId);
            }
            channel.basicAck(deliveryTag, false);
        }catch (Exception e){
            log.info("派单死信队列发生异常" + e.getMessage());
            mqMessageLogService.saveAssignTimeoutFailedLog(transportOrderId, e.getMessage());
            channel.basicNack(deliveryTag, false, false);
        }
    }
}

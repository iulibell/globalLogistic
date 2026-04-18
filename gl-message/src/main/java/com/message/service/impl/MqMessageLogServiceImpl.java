package com.message.service.impl;

import cn.ipokerface.snowflake.SnowflakeIdGenerator;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.constant.RabbitConstant;
import com.message.dao.MqMessageLogDao;
import com.message.entity.MqMessageLog;
import com.message.service.MqMessageLogService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Service
@Transactional
public class MqMessageLogServiceImpl extends ServiceImpl<MqMessageLogDao, MqMessageLog>
        implements MqMessageLogService {
    @Resource
    private SnowflakeIdGenerator snowflakeIdGenerator;

    @Override
    public void saveOrderTimeoutLog(String associatedId) {
        // 生成messageId
        String messageId = LocalDateTime.now()
                .format(DateTimeFormatter
                        .ofPattern("yyyyMMddHHmmss")) + snowflakeIdGenerator.nextId() % 10000;
        MqMessageLog mqMessageLog = new MqMessageLog();
        mqMessageLog.setMessageId(messageId);
        mqMessageLog.setBizId(associatedId);
        mqMessageLog.setExchange(RabbitConstant.ORDER_TTL_EXCHANGE);
        mqMessageLog.setRoutingKey(RabbitConstant.ORDER_TTL_ROUTING_KEY);
        mqMessageLog.setMessage("订单支付超时" + associatedId);
        mqMessageLog.setStatus((short) 1);
        saveLog(mqMessageLog);
    }

    @Override
    public void saveOrderTimeoutFailedLog(String associatedId, String message) {
        // 生成messageId
        String messageId = LocalDateTime.now()
                .format(DateTimeFormatter
                        .ofPattern("yyyyMMddHHmmss")) + snowflakeIdGenerator.nextId() % 10000;
        MqMessageLog mqMessageLog = new MqMessageLog();
        mqMessageLog.setMessageId(messageId);
        mqMessageLog.setBizId(associatedId);
        mqMessageLog.setExchange(RabbitConstant.ORDER_TTL_EXCHANGE);
        mqMessageLog.setRoutingKey(RabbitConstant.ORDER_TTL_ROUTING_KEY);
        mqMessageLog.setMessage("订单延时消费失败" + message);
        mqMessageLog.setStatus((short) 2);
        saveLog(mqMessageLog);
    }

    @Override
    public void saveInboundTimeoutLog(String applyId) {
        String messageId = LocalDateTime.now()
                .format(DateTimeFormatter
                        .ofPattern("yyyyMMddHHmmss")) + snowflakeIdGenerator.nextId() % 10000;
        MqMessageLog mqMessageLog = new MqMessageLog();
        mqMessageLog.setMessageId(messageId);
        mqMessageLog.setBizId(applyId);
        mqMessageLog.setExchange(RabbitConstant.INBOUND_TTL_EXCHANGE);
        mqMessageLog.setRoutingKey(RabbitConstant.INBOUND_TTL_ROUTING_KEY);
        mqMessageLog.setMessage("入库申请支付超时" + applyId);
        mqMessageLog.setStatus((short) 1);
        saveLog(mqMessageLog);
    }

    @Override
    public void saveInboundTimeoutFailedLog(String applyId, String message) {
        String messageId = LocalDateTime.now()
                .format(DateTimeFormatter
                        .ofPattern("yyyyMMddHHmmss")) + snowflakeIdGenerator.nextId() % 10000;
        MqMessageLog mqMessageLog = new MqMessageLog();
        mqMessageLog.setMessageId(messageId);
        mqMessageLog.setBizId(applyId);
        mqMessageLog.setExchange(RabbitConstant.INBOUND_TTL_EXCHANGE);
        mqMessageLog.setRoutingKey(RabbitConstant.INBOUND_TTL_ROUTING_KEY);
        mqMessageLog.setMessage("入库申请延时消费失败" + message);
        mqMessageLog.setStatus((short) 2);
        saveLog(mqMessageLog);
    }

    @Override
    public void saveAssignTimeoutLog(String transportOrderId) {
        String messageId = LocalDateTime.now()
                .format(DateTimeFormatter
                        .ofPattern("yyyyMMddHHmmss")) + snowflakeIdGenerator.nextId() % 10000;
        MqMessageLog mqMessageLog = new MqMessageLog();
        mqMessageLog.setMessageId(messageId);
        mqMessageLog.setBizId(transportOrderId);
        mqMessageLog.setExchange(RabbitConstant.ASSIGN_TTL_EXCHANGE);
        mqMessageLog.setRoutingKey(RabbitConstant.ASSIGN_TTL_ROUTING_KEY);
        mqMessageLog.setMessage("运输单派单窗口到期,已自动加价重派" + transportOrderId);
        mqMessageLog.setStatus((short) 1);
        saveLog(mqMessageLog);
    }

    @Override
    public void saveAssignTimeoutFailedLog(String transportOrderId, String message) {
        String messageId = LocalDateTime.now()
                .format(DateTimeFormatter
                        .ofPattern("yyyyMMddHHmmss")) + snowflakeIdGenerator.nextId() % 10000;
        MqMessageLog mqMessageLog = new MqMessageLog();
        mqMessageLog.setMessageId(messageId);
        mqMessageLog.setBizId(transportOrderId);
        mqMessageLog.setExchange(RabbitConstant.ASSIGN_TTL_EXCHANGE);
        mqMessageLog.setRoutingKey(RabbitConstant.ASSIGN_TTL_ROUTING_KEY);
        mqMessageLog.setMessage("运输单派单延时消费失败" + message);
        mqMessageLog.setStatus((short) 2);
        saveLog(mqMessageLog);
    }

    private void saveLog(MqMessageLog mqMessageLog) {
        save(mqMessageLog);
    }
}

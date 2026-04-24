package com.wms.config;

import com.constant.RabbitConstant;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

/**
 * 由消息发布方（logi-wms）兜底声明入库审核 TTL / DLQ 拓扑。
 * 避免 gl-message 尚未启动时，发送到 inbound_ttl_exchange 出现 NOT_FOUND(404)。
 */
@Configuration
public class InboundRabbitTopologyConfig {

    @Bean
    public Queue inboundTtlQueue() {
        Map<String, Object> args = new HashMap<>();
        args.put("x-message-ttl", RabbitConstant.INBOUND_TTL_EXPIRE);
        args.put("x-dead-letter-exchange", RabbitConstant.INBOUND_DEAD_EXCHANGE);
        args.put("x-dead-letter-routing-key", RabbitConstant.INBOUND_DEAD_ROUTING_KEY);
        return new Queue(RabbitConstant.INBOUND_TTL_QUEUE, true, false, false, args);
    }

    @Bean
    public DirectExchange inboundTtlExchange() {
        return new DirectExchange(RabbitConstant.INBOUND_TTL_EXCHANGE, true, false);
    }

    @Bean
    public Binding inboundTtlBinding() {
        return BindingBuilder.bind(inboundTtlQueue()).to(inboundTtlExchange()).with(RabbitConstant.INBOUND_TTL_ROUTING_KEY);
    }

    @Bean
    public Queue inboundDeadQueue() {
        return new Queue(RabbitConstant.INBOUND_DEAD_QUEUE, true, false, false);
    }

    @Bean
    public DirectExchange inboundDeadExchange() {
        return new DirectExchange(RabbitConstant.INBOUND_DEAD_EXCHANGE, true, false);
    }

    @Bean
    public Binding inboundDeadBinding() {
        return BindingBuilder.bind(inboundDeadQueue()).to(inboundDeadExchange()).with(RabbitConstant.INBOUND_DEAD_ROUTING_KEY);
    }
}

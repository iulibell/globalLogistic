package com.tms.config;

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
 * 由消息发布方（logi-tms）兜底声明派单 TTL / DLQ 拓扑。
 * 避免 gl-message 尚未启动时，发送到 assign_ttl_exchange 出现 NOT_FOUND(404)。
 */
@Configuration
public class AssignRabbitTopologyConfig {

    @Bean
    public Queue assignTtlQueue() {
        Map<String, Object> args = new HashMap<>();
        args.put("x-message-ttl", RabbitConstant.ASSIGN_TTL_EXPIRE);
        args.put("x-dead-letter-exchange", RabbitConstant.ASSIGN_DEAD_EXCHANGE);
        args.put("x-dead-letter-routing-key", RabbitConstant.ASSIGN_DEAD_ROUTING_KEY);
        return new Queue(RabbitConstant.ASSIGN_TTL_QUEUE, true, false, false, args);
    }

    @Bean
    public DirectExchange assignTtlExchange() {
        return new DirectExchange(RabbitConstant.ASSIGN_TTL_EXCHANGE, true, false);
    }

    @Bean
    public Binding assignTtlBinding() {
        return BindingBuilder.bind(assignTtlQueue()).to(assignTtlExchange()).with(RabbitConstant.ASSIGN_TTL_ROUTING_KEY);
    }

    @Bean
    public Queue assignDeadQueue() {
        return new Queue(RabbitConstant.ASSIGN_DEAD_QUEUE, true, false, false);
    }

    @Bean
    public DirectExchange assignDeadExchange() {
        return new DirectExchange(RabbitConstant.ASSIGN_DEAD_EXCHANGE, true, false);
    }

    @Bean
    public Binding assignDeadBinding() {
        return BindingBuilder.bind(assignDeadQueue()).to(assignDeadExchange()).with(RabbitConstant.ASSIGN_DEAD_ROUTING_KEY);
    }
}

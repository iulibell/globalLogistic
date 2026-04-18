package com.message.config;

import com.constant.RabbitConstant;
import org.springframework.amqp.core.*;
import org.springframework.amqp.support.converter.JacksonJsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class RabbitMqConfig {

    @SuppressWarnings("null")
    @Bean
    public MessageConverter rabbitMessageConverter() {
        return new JacksonJsonMessageConverter();
    }

    /*=========== 订单锁延时队列 ============ */

    @Bean
    public Queue orderTtlQueue() {
        Map<String, Object> args = new HashMap<>();
        args.put("x-message-ttl", RabbitConstant.ORDER_TTL_EXPIRE);
        args.put("x-dead-letter-exchange", RabbitConstant.ORDER_DEAD_EXCHANGE);
        args.put("x-dead-letter-routing-key", RabbitConstant.ORDER_DEAD_ROUTING_KEY);
        return new Queue(
                RabbitConstant.ORDER_TTL_QUEUE,
                true,
                false,
                true,
                args
        );
    }

    @Bean
    public Exchange orderTtlExchange() {
        return new DirectExchange(RabbitConstant.ORDER_TTL_EXCHANGE,true,false);
    }

    @Bean
    public Binding orderTtlBinding() {
        return BindingBuilder
                .bind(orderTtlQueue())
                .to(orderTtlExchange())
                .with(RabbitConstant.ORDER_TTL_ROUTING_KEY)
                .noargs();
    }

    @Bean
    public Queue orderDeadQueue() {
        return new Queue(
                RabbitConstant.ORDER_DEAD_QUEUE,
                true,
                false,
                false,
                null
        );
    }

    @Bean
    public Exchange orderDeadExchange() {
        return new DirectExchange(RabbitConstant.ORDER_DEAD_EXCHANGE);
    }

    @Bean
    public Binding orderDeadBinding() {
        return BindingBuilder
                .bind(orderDeadQueue())
                .to(orderDeadExchange())
                .with(RabbitConstant.ORDER_DEAD_ROUTING_KEY)
                .noargs();
    }

    /*=========== 申请入库订单延时队列 ============ */

    @Bean
    public Queue inboundTtlQueue() {
        Map<String, Object> args = new HashMap<>();
        args.put("x-message-ttl", RabbitConstant.INBOUND_TTL_EXPIRE);
        args.put("x-dead-letter-exchange", RabbitConstant.INBOUND_DEAD_EXCHANGE);
        args.put("x-dead-letter-routing-key", RabbitConstant.INBOUND_DEAD_ROUTING_KEY);
        return new Queue(
                RabbitConstant.INBOUND_TTL_QUEUE,
                true,
                false,
                true,
                args
        );
    }

    @Bean
    public Exchange inboundTtlExchange() {
        return new DirectExchange(RabbitConstant.INBOUND_TTL_EXCHANGE,true,false);
    }

    @Bean
    public Binding inboundTtlBinding() {
        return BindingBuilder
                .bind(inboundTtlQueue())
                .to(inboundTtlExchange())
                .with(RabbitConstant.INBOUND_TTL_ROUTING_KEY)
                .noargs();
    }

    @Bean
    public Queue inboundDeadQueue() {
        return new Queue(
                RabbitConstant.INBOUND_DEAD_QUEUE,
                true,
                false,
                false,
                null
        );
    }

    @Bean
    public Exchange inboundDeadExchange() {
        return new DirectExchange(RabbitConstant.INBOUND_DEAD_EXCHANGE);
    }

    @Bean
    public Binding inboundDeadBinding() {
        return BindingBuilder
                .bind(inboundDeadQueue())
                .to(inboundDeadExchange())
                .with(RabbitConstant.INBOUND_DEAD_ROUTING_KEY)
                .noargs();
    }

    /*=========== 分配司机延时队列 ============ */

    @Bean
    public Queue assignTtlQueue() {
        Map<String, Object> args = new HashMap<>();
        args.put("x-message-ttl", RabbitConstant.ASSIGN_TTL_EXPIRE);
        args.put("x-dead-letter-exchange", RabbitConstant.ASSIGN_DEAD_EXCHANGE);
        args.put("x-dead-letter-routing-key", RabbitConstant.ASSIGN_DEAD_ROUTING_KEY);
        return new Queue(
                RabbitConstant.ASSIGN_TTL_QUEUE,
                true,
                false,
                true,
                args
        );
    }

    @Bean
    public Exchange assignTtlExchange() {
        return new DirectExchange(RabbitConstant.ASSIGN_TTL_EXCHANGE,true,false);
    }

    @Bean
    public Binding assignTtlBinding() {
        return BindingBuilder
                .bind(assignTtlQueue())
                .to(assignTtlExchange())
                .with(RabbitConstant.ASSIGN_TTL_ROUTING_KEY)
                .noargs();
    }

    @Bean
    public Queue assignDeadQueue() {
        return new Queue(
                RabbitConstant.ASSIGN_DEAD_QUEUE,
                true,
                false,
                false,
                null
        );
    }

    @Bean
    public Exchange assignDeadExchange() {
        return new DirectExchange(RabbitConstant.ASSIGN_DEAD_EXCHANGE);
    }

    @Bean
    public Binding assignDeadBinding() {
        return BindingBuilder
                .bind(assignDeadQueue())
                .to(assignDeadExchange())
                .with(RabbitConstant.ASSIGN_DEAD_ROUTING_KEY)
                .noargs();
    }
}

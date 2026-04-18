package com.constant;

public class RabbitConstant {
    /**
     * 订单锁定延时队列名称
     */
    public static final String ORDER_TTL_QUEUE = "order_ttl_queue";
    public static final String ORDER_TTL_EXCHANGE = "order_ttl_exchange";
    public static final String ORDER_TTL_ROUTING_KEY = "order_ttl_routing_key";
    /**
     * 订单锁定死信队列名称
     */
    public static final String ORDER_DEAD_QUEUE = "order_dead_queue";
    public static final String ORDER_DEAD_EXCHANGE = "order_dead_exchange";
    public static final String ORDER_DEAD_ROUTING_KEY = "order_dead_routing_key";
    /**
     * 订单锁定队列过期时间,31min后过过期
     */
    public static final int ORDER_TTL_EXPIRE = 1000 * 60 * 31;
    /**
     * 上架申请订单延时队列名称
     */
    public static final String INBOUND_TTL_QUEUE = "inbound_ttl_queue";
    public static final String INBOUND_TTL_EXCHANGE = "inbound_ttl_exchange";
    public static final String INBOUND_TTL_ROUTING_KEY = "inbound_ttl_routing_key";
    /**
     * 上架申请订单死信队列名称
     */
    public static final String INBOUND_DEAD_QUEUE = "inbound_dead_queue";
    public static final String INBOUND_DEAD_EXCHANGE = "inbound_dead_exchange";
    public static final String INBOUND_DEAD_ROUTING_KEY = "inbound_dead_routing_key";
    /**
     * 上架申请订单队列过期时间,1h1min后过期
     */
    public static final int INBOUND_TTL_EXPIRE = 1000 * 60 * 61;

    /**
     * 派单延时队列名称
     */
    public static final String ASSIGN_TTL_QUEUE = "assign_ttl_queue";
    public static final String ASSIGN_TTL_EXCHANGE = "assign_ttl_exchange";
    public static final String ASSIGN_TTL_ROUTING_KEY = "assign_ttl_routing_key";
    /**
     * 派单死信队列名称
     */
    public static final String ASSIGN_DEAD_QUEUE = "assign_dead_queue";
    public static final String ASSIGN_DEAD_EXCHANGE = "assign_dead_exchange";
    public static final String ASSIGN_DEAD_ROUTING_KEY = "assign_dead_routing_key";
    /**
     * 派单队列过期时间,31min后过期
     */
    public static final int ASSIGN_TTL_EXPIRE = 1000 * 60 * 16;
}

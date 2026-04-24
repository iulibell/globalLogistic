package com.constant;

public class RedisConstant {
    /**
     * 申请注册后等待重新注册
     * 时间24h
     */
    public static final String REGIS_KEY_PREFIX = "register:";
    public static final int REGIS_EXPIRE_TIME = 24;

    /** 注册短信/验证码：手机号 -> 验证码，5 分钟有效 */
    public static final String REGIS_CAPTCHA_PREFIX = "register:captcha:";
    public static final int REGIS_CAPTCHA_EXPIRE_MINUTES = 5;

    /** 同一手机号发送验证码最小间隔（秒） */
    public static final String REGIS_CAPTCHA_SEND_PREFIX = "register:captcha:send:";
    public static final int REGIS_CAPTCHA_SEND_INTERVAL_SECONDS = 60;

    /**
     * 电商订单待支付
     * 时间30min
     */
    public static final String ORDER_KEY_PREFIX = "order:";
    public static final int ORDER_EXPIRE_TIME = 30;

    /**
     * 申请上架订单待支付
     * 时间1h
     */
    public static final String INBOUND_KEY_PREFIX = "inbound:";
    public static final int INBOUND_EXPIRE_TIME = 6;

    /**
     * 分配司机的派单待接单(没人接单将重新分配并增加fee)
     * 时间30min
     */
    public static final String ASSIGN_KEY_PREFIX = "assign:";
    public static final int ASSIGN_EXPIRE_TIME = 15;

    /**
     * 查询物流单后将物流单存到redis里
     * 时间30min
     */
    public static final String LOGI_KEY_PREFIX = "logi:";
    public static final int LOGI_EXPIRE_TIME = 30;
}

package com.system.config;

import cn.dev33.satoken.jwt.StpLogicJwtForMixin;
import cn.dev33.satoken.stp.StpInterface;
import cn.dev33.satoken.stp.StpLogic;
import com.system.satoken.CommonSaTokenPermissionInterface;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;

/**
 * 全服务统一 JWT + Redis（Mixin）：登录在 gl-admin 等节点写入的 Session（含 permissions）可被网关与其它微服务读取，
 * 避免网关用默认 StpLogic 导致 JWT 验票失败（401），或 Simple 模式读不到 Redis 会话导致权限异常。
 */
public class SaTokenJwtAutoConfig {

    @Bean
    @Primary
    @ConditionalOnClass(StpLogicJwtForMixin.class)
    public StpLogic stpLogicJwtForMixin() {
        return new StpLogicJwtForMixin();
    }

    /** 与 LoginAndLogoutService 写入的 session.permissions 对齐；gl-admin 等必须注册，否则 checkPermission("admin") 恒失败 */
    @Bean
    public StpInterface stpInterface() {
        return new CommonSaTokenPermissionInterface();
    }
}

package com.gateway.config;

import com.api.CommonResult;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.web.server.ServerWebExchange;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.json.JsonWriteFeature;
import com.fasterxml.jackson.databind.json.JsonMapper;

import cn.dev33.satoken.exception.NotLoginException;
import cn.dev33.satoken.exception.NotPermissionException;
import cn.dev33.satoken.reactor.context.SaReactorSyncHolder;
import cn.dev33.satoken.reactor.filter.SaReactorFilter;

@Configuration
public class SaTokenGatewayConfig {

    /**
     * 必须在 CORS 相关 WebFilter 之后执行，否则 OPTIONS 预检会先走鉴权失败，响应里没有正确的 Allow-Headers。
     */
    @Bean
    @Order(Ordered.LOWEST_PRECEDENCE)
    public SaReactorFilter saReactorFilter(WhitelistConfig whitelistConfig) {
        // 网关鉴权异常正文经 Sa-Reactor 写出时，部分环境下非 ASCII 会按错误码表编码导致乱码；转义后仅含 ASCII，前端 JSON 解析后中文正常
        final JsonMapper errorJsonMapper = JsonMapper.builder()
                .configure(JsonWriteFeature.ESCAPE_NON_ASCII, true)
                .build();
        return new SaReactorFilter()
                .addInclude("/**")
                .setAuth(obj -> {
                    ServerWebExchange ex = SaReactorSyncHolder.getContext();
                    if (ex != null && HttpMethod.OPTIONS.equals(ex.getRequest().getMethod())) {
                        return;
                    }
                    String path = ex != null ? ex.getRequest().getURI().getPath() : "";

                }).setError(e -> {
                    // 统一异常返回 JSON
                    ServerWebExchange exchange = SaReactorSyncHolder.getContext();
                    HttpHeaders headers = exchange.getResponse().getHeaders();
                    headers.set("Content-Type", "application/json; charset=utf-8");
                    headers.set("Access-Control-Allow-Origin", "*");
                    headers.set(
                            "Access-Control-Allow-Headers",
                            "Authorization, Content-Type, Accept, Origin, X-Requested-With, satoken");
                    headers.set("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS, PATCH");
                    headers.set("Cache-Control", "no-cache");

                    CommonResult<?> result;
                    if (e instanceof NotLoginException) {
                        result = CommonResult.unauthorized(null);
                    } else if (e instanceof NotPermissionException) {
                        result = CommonResult.forbidden();
                    } else {
                        result = CommonResult.failed(e.getMessage());
                    }
                    // Sa-Reactor 对返回值可能走 toString()，直接 return CommonResult 会导致正文为 Java 字符串而非 JSON
                    try {
                        return errorJsonMapper.writeValueAsString(result);
                    } catch (JsonProcessingException ex) {
                        return "{\"code\":500,\"message\":\"响应序列化失败\",\"data\":null}";
                    }
                }).setExcludeList(whitelistConfig.getUrls());
    }
}

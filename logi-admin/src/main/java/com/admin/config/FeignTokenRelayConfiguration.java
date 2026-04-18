package com.admin.config;

import feign.RequestInterceptor;
import feign.RequestTemplate;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

/**
 * 将当前请求的 satoken / Authorization 透传给 Feign 调用的下游服务，否则聚合接口在下游会 401。
 */
@Configuration
public class FeignTokenRelayConfiguration {

    @Bean
    public RequestInterceptor feignAuthRequestInterceptor() {
        return (RequestTemplate template) -> {
            var attrs = RequestContextHolder.getRequestAttributes();
            if (!(attrs instanceof ServletRequestAttributes sra)) {
                return;
            }
            HttpServletRequest req = sra.getRequest();
            String satoken = req.getHeader("satoken");
            if (satoken != null && !satoken.isBlank()) {
                template.header("satoken", satoken);
            }
            String authorization = req.getHeader("Authorization");
            if (authorization != null && !authorization.isBlank()) {
                template.header("Authorization", authorization);
            }
            String internal = req.getHeader("X-Internal-Token");
            if (internal != null && !internal.isBlank()) {
                template.header("X-Internal-Token", internal);
            }
        };
    }
}

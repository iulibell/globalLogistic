package com.security;

import cn.dev33.satoken.router.SaRouter;
import cn.dev33.satoken.stp.StpUtil;
import com.config.InternalCallbackSecurityProperties;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.server.ServerWebExchange;

import java.util.ArrayList;
import java.util.List;

/**
 * 内部 /sys 回调（可选令牌）→ 其余路径登录 → 按角色 permission。
 */
public final class SaTokenRouteChecks {

    private static final String[] BUILTIN_ANON = {
            // Prometheus / 健康检查（各服务直连端口抓取，勿对公网暴露）
            "/actuator/**",
            "/system/register",
            "/system/register/sendCaptcha",
            "/admin/getRegisterFromSys",
            "/error",
            "/favicon.ico",
            "/swagger-ui.html",
            "/swagger-ui/**",
            "/v3/api-docs",
            "/v3/api-docs/**",
            "/doc.html",
            "/webjars/**",
            "/system/login",
            "/auth/login",
            "/auth/register",
            "/auth/register/sendCaptcha",
            // 门户物流查询（按运单号只读查询）
            "/tms/getLogisticById",
            // 门户/追踪页等多处拉取展示文案，无需登录（仍只读启用项）
            "/admin/sys/getDictionary/**",
            // 商城 mall-admin / mall-portal 经 Feign 调用（无物流域 token、JWT 密钥亦不同），视为内网联调入口
            "/wms/addInboundApply",
            "/wms/payForInbound",
            "/wms/outbound/create",
            // 商户上架选仓：portal Feign 拉取启用仓库列表（只读）
            "/wms/getAvailableWarehouse"
    };

    private SaTokenRouteChecks() {
    }

    public static List<String> mergedWhitelist(List<String> configuredWhitelist) {
        List<String> noLogin = new ArrayList<>();
        for (String p : BUILTIN_ANON) {
            noLogin.add(p);
        }
        if (configuredWhitelist != null) {
            for (String p : configuredWhitelist) {
                if (p != null && !p.isBlank()) {
                    noLogin.add(p.trim());
                }
            }
        }
        return noLogin;
    }

    public static void runServlet(List<String> configuredWhitelist, InternalCallbackSecurityProperties internal) {
        var attrs = RequestContextHolder.getRequestAttributes();
        if (!(attrs instanceof ServletRequestAttributes sra)) {
            // 无 Servlet 请求属性时 SaHolder 未初始化，禁止调用 SaRouter
            return;
        }
        HttpServletRequest request = sra.getRequest();
        String path = InternalCallbackAccessVerifier.servletRelativePath(request);
        if (handleInternalCallbackPath(path, request, null, internal)) {
            return;
        }
        applyUserRouteRules(configuredWhitelist);
    }

    public static void runReactive(ServerWebExchange exchange, List<String> configuredWhitelist, InternalCallbackSecurityProperties internal) {
        String path = normalizeUriPath(exchange.getRequest().getURI().getPath());
        if (handleInternalCallbackPath(path, null, exchange, internal)) {
            return;
        }
        applyUserRouteRules(configuredWhitelist);
    }

    private static boolean handleInternalCallbackPath(
            String path,
            HttpServletRequest servletRequest,
            ServerWebExchange exchange,
            InternalCallbackSecurityProperties internal
    ) {
        if (!InternalCallbackAccessVerifier.isInternalCallbackPath(path)) {
            return false;
        }
        if (!internal.isEnabled()) {
            return true;
        }
        if (servletRequest != null) {
            InternalCallbackAccessVerifier.verifyServlet(servletRequest, internal);
        } else if (exchange != null) {
            InternalCallbackAccessVerifier.verifyReactive(exchange, internal);
        }
        return true;
    }

    private static void applyUserRouteRules(List<String> configuredWhitelist) {
        List<String> noLogin = mergedWhitelist(configuredWhitelist);
        String[] anon = noLogin.toArray(new String[0]);

        SaRouter.match("/**").notMatch(anon).check(r -> StpUtil.checkLogin());

        SaRouter.match("/tms/driver/**").check(r -> StpUtil.checkPermission("driver"));
        SaRouter.match("/tms/manager/**").check(r -> StpUtil.checkPermission("manager"));

        SaRouter.match("/wms/**")
                .notMatch("/wms/sys/**", "/wms/addInboundApply", "/wms/payForInbound", "/wms/outbound/create",
                        "/wms/getAvailableWarehouse")
                .check(r -> StpUtil.checkPermission("keeper"));

        SaRouter.match("/oms/**").notMatch("/oms/sys/**").check(r -> StpUtil.checkLogin());

        SaRouter.match("/admin/reviewer/**").check(r -> StpUtil.checkPermission("reviewer"));
        SaRouter.match("/admin/manager/**").check(r -> StpUtil.checkPermission("manager"));
        SaRouter.match("/admin/super/**").check(r -> StpUtil.checkPermission("super"));
        SaRouter.match("/admin/sys/**")
                .notMatch("/admin/sys/getDictionary/**")
                .check(r -> StpUtil.checkPermission("super"));

        SaRouter.match("/system/admin/**").check(r -> StpUtil.checkPermission("manager"));
    }

    private static String normalizeUriPath(String uri) {
        if (uri == null) {
            return "/";
        }
        int q = uri.indexOf('?');
        String path = q >= 0 ? uri.substring(0, q) : uri;
        if (!path.startsWith("/")) {
            return "/" + path;
        }
        return path;
    }
}

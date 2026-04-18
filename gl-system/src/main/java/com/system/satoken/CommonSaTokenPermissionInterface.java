package com.system.satoken;

import cn.dev33.satoken.SaManager;
import cn.dev33.satoken.session.SaSession;
import cn.dev33.satoken.stp.StpInterface;
import cn.dev33.satoken.stp.StpLogic;

import java.util.ArrayList;
import java.util.List;

public class CommonSaTokenPermissionInterface implements StpInterface {

    private static StpLogic stpLogicFor(String loginType) {
        String type = (loginType == null || loginType.isBlank()) ? "login" : loginType;
        StpLogic registered = SaManager.getStpLogic(type);
        return registered != null ? registered : new StpLogic(type);
    }

    /**
     * token 解析出的 loginId 可能与登录时 {@code login(Long)} 的键类型不一致（如 String "23" vs Long 23），
     * 会导致 getSessionByLoginId 取不到会话 → 权限恒为空。
     */
    private static SaSession resolveSession(StpLogic stpLogic, Object loginId) {
        if (loginId == null) {
            return null;
        }
        SaSession s = stpLogic.getSessionByLoginId(loginId, false);
        if (s != null) {
            return s;
        }
        if (loginId instanceof Number n) {
            s = stpLogic.getSessionByLoginId(String.valueOf(n.longValue()), false);
            if (s != null) {
                return s;
            }
        }
        if (loginId instanceof String str && !str.isEmpty()) {
            try {
                s = stpLogic.getSessionByLoginId(Long.parseLong(str), false);
                if (s != null) {
                    return s;
                }
            } catch (NumberFormatException ignored) {
                /* keep */
            }
        }
        return null;
    }

    private static List<String> toPermissionStrings(Object obj) {
        if (!(obj instanceof List<?> list)) {
            return null;
        }
        List<String> out = new ArrayList<>(list.size());
        for (Object o : list) {
            if (o != null) {
                out.add(o.toString());
            }
        }
        return out.isEmpty() ? null : out;
    }

    @Override
    public List<String> getPermissionList(Object loginId, String loginType) {
        StpLogic stpLogic = stpLogicFor(loginType);
        SaSession session = resolveSession(stpLogic, loginId);
        if (session == null) {
            return null;
        }
        List<String> perms = toPermissionStrings(session.get("permissions"));
        if (perms == null) {
            return null;
        }
        return perms;
    }

    @Override
    public List<String> getRoleList(Object loginId, String loginType) {
        return null;
    }
}

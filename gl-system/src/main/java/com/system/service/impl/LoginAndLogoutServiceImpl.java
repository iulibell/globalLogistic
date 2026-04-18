package com.system.service.impl;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

import cn.dev33.satoken.stp.SaTokenInfo;
import cn.dev33.satoken.stp.StpLogic;
import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.digest.BCrypt;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.constant.AuthConstant;
import com.exception.Assert;
import com.system.dao.SysUserDao;
import com.system.dto.LoginParamDto;
import com.system.dto.UserSessionDto;
import com.system.entity.SysUser;
import com.system.service.CacheService;
import com.system.service.LoginAndLogoutService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class LoginAndLogoutServiceImpl implements LoginAndLogoutService {

    /** 与 {@link com.system.config.SaTokenJwtAutoConfig} 中 {@code @Primary} 的 {@link StpLogic} 一致（JWT + Redis Session） */
    @Resource
    private StpLogic stpLogic;

    private static final Map<String, String> LOGI_ALIAS_TO_ROLE;
    private static final Map<String, List<String>> PERMISSIONS_BY_ROLE;

    static {
        Map<String, String> logiAlias = new HashMap<>();
        putLogi(logiAlias, "1", "super");
        putLogi(logiAlias, "2", "manager");
        putLogi(logiAlias, "3", "keeper");
        putLogi(logiAlias, "4", "driver");
        putLogi(logiAlias, "5", "reviewer");
        LOGI_ALIAS_TO_ROLE = Collections.unmodifiableMap(logiAlias);

        PERMISSIONS_BY_ROLE = Map.of(
                "super", List.of("super", "manager", "keeper", "driver", "reviewer"),
                "manager", List.of("manager"),
                "keeper", List.of("keeper"),
                "driver", List.of("driver"),
                "reviewer", List.of("reviewer"));
    }

    private static void putLogi(Map<String, String> aliasToRole, String storedDigit, String roleKey) {
        aliasToRole.put(storedDigit, roleKey);
        aliasToRole.put(roleKey, roleKey);
    }

    @Resource
    private SysUserDao sysUserDao;
    @Resource
    private CacheService cacheService;

    /**
     * 注册等场景写入的 userType 可能为 1–5，权限与登录入口校验统一成英文角色 key。
     */
    public static String normalizeUserTypeToRoleKey(String userType) {
        String t = trimOrNull(userType);
        return t == null ? null : LOGI_ALIAS_TO_ROLE.get(t);
    }

    private static String trimOrNull(String userType) {
        if (StrUtil.isBlank(userType)) {
            return null;
        }
        return userType.trim();
    }

    private static String logiStpLoginId(SysUser user) {
        if (StrUtil.isNotBlank(user.getUserId())) {
            try {
                return user.getUserId().trim();
            } catch (NumberFormatException ignored) {
                Assert.fail("发生了异常,请稍后再试!");
            }
        }
        Assert.fail("账号数据异常，请联系管理员");
        return "";
    }

    @Override
    @SuppressWarnings("null")
    public SaTokenInfo login(LoginParamDto loginParamDto, String requiredRoleKey) {
        if (StrUtil.isEmpty(loginParamDto.getUsername()) || StrUtil.isEmpty(loginParamDto.getPassword())) {
            Assert.fail("账号或密码不能为空");
        }
        if (StrUtil.isBlank(requiredRoleKey)) {
            Assert.fail("登录入口角色无效");
        }

        SysUser sysUser = sysUserDao.selectOne(new LambdaQueryWrapper<SysUser>()
                .eq(SysUser::getUsername, loginParamDto.getUsername()));
        if (sysUser == null) {
            Assert.fail("账号不存在");
        }
        String storedHash = sysUser.getPassword();
        if (StrUtil.isEmpty(storedHash)) {
            Assert.fail("账号数据异常，请联系管理员");
        }
        if (!BCrypt.checkpw(loginParamDto.getPassword(), storedHash)) {
            Assert.fail("密码错误");
        }
        if (!Objects.equals(sysUser.getStatus(), (short) 1)) {
            Assert.fail("账号已被禁用");
        }
        String actualRole = normalizeUserTypeToRoleKey(sysUser.getUserType());
        if (actualRole == null) {
            Assert.fail("账号类型无效，请联系管理员");
        }
        if (!requiredRoleKey.equals(actualRole)) {
            Assert.fail("所选角色与账号不符，请在上方选择正确的角色后再登录");
        }

        String userId = logiStpLoginId(sysUser);
        stpLogic.login(userId);

        UserSessionDto sessionDto = new UserSessionDto();
        sessionDto.setUserId(userId);
        sessionDto.setUsername(sysUser.getUsername());
        sessionDto.setUserType(sysUser.getUserType());
        sessionDto.setNickname(sysUser.getNickname());
        sessionDto.setPhone(sysUser.getPhone());

        stpLogic.getSession().set(AuthConstant.STP_ADMIN_INFO, UserSessionDto.toSessionMap(sessionDto));

        List<String> permissions = getPermissionsByRoleKey(actualRole);
        stpLogic.getSession().set("permissions", permissions);
        cacheService.setLoginStatus(userId, sysUser, TimeUnit.HOURS);
        log.info("登录成功，用户ID：{}，角色：{}", userId, actualRole);
        return stpLogic.getTokenInfo();
    }

    @Override
    public Map<String, Object> buildLoginResponse(SaTokenInfo saTokenInfo, String tokenPrefix) {
        Map<String, Object> tokenMap = new HashMap<>();
        tokenMap.put("token", saTokenInfo.getTokenValue());
        tokenMap.put("tokenHead", tokenPrefix.trim() + " ");
        UserSessionDto dto = UserSessionDto.fromSessionValue(stpLogic.getSession().get(AuthConstant.STP_ADMIN_INFO));
        String displayName = "";
        String resolvedRoleKey = null;
        if (dto != null) {
            displayName = StrUtil.blankToDefault(StrUtil.nullToEmpty(dto.getNickname()), dto.getUsername());
            if (StrUtil.isNotBlank(dto.getUserId())) {
                tokenMap.put("userId", dto.getUserId());
            }
            tokenMap.put("username", dto.getUsername());
            resolvedRoleKey = normalizeUserTypeToRoleKey(dto.getUserType());
            if (resolvedRoleKey != null) {
                tokenMap.put("role", resolvedRoleKey);
            }
        }
        tokenMap.put("nickname", displayName);
        @SuppressWarnings("unchecked")
        List<String> perms = (List<String>) stpLogic.getSession().get("permissions");
        if (perms == null || perms.isEmpty()) {
            if (resolvedRoleKey != null) {
                perms = getPermissionsByRoleKey(resolvedRoleKey);
            } else {
                perms = Collections.emptyList();
            }
        }
        tokenMap.put("permissions", perms);
        return tokenMap;
    }

    @Override
    public void logout() {
        UserSessionDto sessionDto = UserSessionDto.fromSessionValue(stpLogic.getSession().get(AuthConstant.STP_ADMIN_INFO));
        if (sessionDto != null && StrUtil.isNotBlank(sessionDto.getUserId())) {
            try {
                cacheService.removeLoginStatus(Long.parseLong(sessionDto.getUserId().trim()));
            } catch (NumberFormatException ignored) {
                /* userId 非数字则跳过缓存清理 */
            }
        }
        stpLogic.logout();
    }

    private List<String> getPermissionsByRoleKey(String roleKey) {
        return PERMISSIONS_BY_ROLE.getOrDefault(roleKey, Collections.emptyList());
    }
}


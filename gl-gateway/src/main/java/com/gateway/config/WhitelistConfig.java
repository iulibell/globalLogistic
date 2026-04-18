package com.gateway.config;

import java.util.Collections;
import java.util.List;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 网关白名单配置
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Component
@ConfigurationProperties(prefix="secure.ignore")
public class WhitelistConfig {
    private List<String> urls = Collections.emptyList();

    public List<String> getUrls() {
        return urls == null ? Collections.emptyList() : urls;
    }
}

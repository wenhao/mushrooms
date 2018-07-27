package com.github.wenhao.common.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;
import java.util.List;

import static java.util.Objects.isNull;

@Data
@Configuration
@ConfigurationProperties("mushrooms")
public class CachingConfigurationProperties {
    private String key;
    private List<String> headers;

    @PostConstruct
    public void init() {
        if (isNull(key)) {
            key = "MUSHROOMS-CACHE";
        }
    }
}

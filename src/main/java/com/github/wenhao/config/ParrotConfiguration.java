package com.github.wenhao.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
@ConfigurationProperties("http.cache")
@Data
public class ParrotConfiguration {
    private String key;
    private List<String> headers;
    private List<String> cookies;
}

package com.github.wenhao.config;

import lombok.Data;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;
import java.util.List;

import static java.util.Objects.isNull;

@Configuration
@ConfigurationProperties("parrot")
@ConditionalOnProperty(value = "parrot.enabled", havingValue = "true", matchIfMissing = true)
@Data
public class ParrotConfiguration {
    private String mapping;
    private String key;
    private List<String> headers;

    @PostConstruct
    public void init() {
        if (isNull(key)) {
            key = "PARROT-CACHE";
        }
    }
}

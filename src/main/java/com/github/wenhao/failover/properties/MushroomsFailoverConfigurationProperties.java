package com.github.wenhao.failover.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Data
@Configuration
@ConfigurationProperties("mushrooms.failover")
public class MushroomsFailoverConfigurationProperties {

    private String key = "MUSHROOMS-FAILOVER-CACHE";
    private List<String> headers;
}

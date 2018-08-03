package com.github.wenhao.failover.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;

@Data
@ConfigurationProperties("mushrooms.failover")
public class MushroomsFailoverConfigurationProperties {

    private String key = "MUSHROOMS-FAILOVER-CACHE";
    private List<String> headers;
}

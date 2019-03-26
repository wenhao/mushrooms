package com.github.wenhao.mushrooms.integration.config;

import com.github.wenhao.mushrooms.stub.domain.Stub;
import lombok.Data;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.List;

@Data
@Component
@ConditionalOnProperty(prefix = "mushrooms.stub", name = "enabled", havingValue = "true")
@ConfigurationProperties(prefix = "mushrooms.stub")
public class MushroomsStubConfiguration {
    private boolean enabled;
    private boolean failover;
    private List<Stub> stubs;
}

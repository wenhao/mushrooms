package com.github.wenhao.mushrooms.integration.config;

import com.github.wenhao.mushrooms.stub.domain.Stub;
import lombok.Data;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Data
@Configuration
@ConditionalOnProperty(prefix = "mushrooms.stub", name = "enabled", havingValue = "true")
public class MushroomsStubConfiguration {
    private boolean enabled;
    private boolean failover;
    private List<Stub> stubs;
}

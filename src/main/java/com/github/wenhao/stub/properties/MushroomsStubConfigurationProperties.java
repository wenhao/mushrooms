package com.github.wenhao.stub.properties;

import com.github.wenhao.stub.domain.Stub;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;

@Data
@ConfigurationProperties("mushrooms.stub")
public class MushroomsStubConfigurationProperties {

    private boolean enabled;
    private List<Stub> stubs;
}

package com.github.wenhao.stub.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties("mushrooms.stub")
public class MushroomsStubConfigurationProperties {

    private String key = "MUSHROOMS-STUB";
    private StubOkHttpConfigurationProperties okhttp;
}

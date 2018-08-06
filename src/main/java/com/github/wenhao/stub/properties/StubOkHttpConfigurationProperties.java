package com.github.wenhao.stub.properties;

import com.github.wenhao.stub.domain.Stub;
import lombok.Data;

import java.util.List;

@Data
public class StubOkHttpConfigurationProperties {

    private boolean enabled;
    private List<Stub> stubs;
}

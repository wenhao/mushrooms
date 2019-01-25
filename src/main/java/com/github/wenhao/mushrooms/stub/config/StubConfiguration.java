package com.github.wenhao.mushrooms.stub.config;

import com.github.wenhao.mushrooms.stub.domain.Stub;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StubConfiguration {

    private boolean enabled;
    private boolean failover;
    private List<Stub> stubs;
}

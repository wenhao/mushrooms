package com.github.wenhao.stub.properties;

import com.github.wenhao.stub.dataloader.ResourceReader;
import com.github.wenhao.stub.domain.Stub;
import lombok.AccessLevel;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;
import java.util.Optional;

@Data
@ConfigurationProperties("mushrooms.stub")
@RequiredArgsConstructor
public class MushroomsStubConfigurationProperties {

    private final ResourceReader resourceReader;
    private boolean enabled;
    private boolean failover;
    @Setter(AccessLevel.NONE)
    private List<Stub> stubs;

    public void setStubs(final List<Stub> stubs) {
        stubs.forEach(stub -> {
            if (!stub.getRequest().getBody().startsWith("xpath:")) {
                stub.getRequest().setBody(Optional.ofNullable(stub.getRequest().getBody()).map(resourceReader::readAsString).orElse(""));
            }
            stub.setResponse(Optional.ofNullable(stub.getResponse()).map(resourceReader::readAsString).orElse(""));
        });
        this.stubs = stubs;
    }
}

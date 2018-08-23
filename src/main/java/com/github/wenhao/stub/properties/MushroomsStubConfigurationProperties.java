package com.github.wenhao.stub.properties;

import com.github.wenhao.stub.dataloader.ResourceReader;
import com.github.wenhao.stub.domain.Stub;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;
import java.util.Optional;

@Getter
@ConfigurationProperties("mushrooms.stub")
@RequiredArgsConstructor
public class MushroomsStubConfigurationProperties {

    private final ResourceReader resourceReader;
    private boolean enabled;
    private List<Stub> stubs;

    public void setEnabled(final boolean enabled) {
        this.enabled = enabled;
    }

    public void setStubs(final List<Stub> stubs) {
        stubs.forEach(stub -> {
            stub.getRequest().setBody(Optional.ofNullable(stub.getRequest().getBody()).map(resourceReader::readAsString).orElse(""));
            stub.setResponse(Optional.ofNullable(stub.getResponse()).map(resourceReader::readAsString).orElse(""));
        });
        this.stubs = stubs;
    }
}

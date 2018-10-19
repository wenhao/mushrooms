package com.github.wenhao.mushrooms.stub.properties;

import com.github.wenhao.mushrooms.stub.dataloader.ResourceReader;
import com.github.wenhao.mushrooms.stub.domain.Stub;
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
            final String body = stub.getRequest().getBody();
            if (!body.startsWith("xpath:") && !body.startsWith("jsonPath:")) {
                stub.getRequest().setBody(Optional.ofNullable(body).map(resourceReader::readAsString).orElse(""));
            }
            stub.setResponse(Optional.ofNullable(stub.getResponse()).map(resourceReader::readAsString).orElse(""));
        });
        this.stubs = stubs;
    }
}

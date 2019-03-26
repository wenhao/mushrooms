package com.github.wenhao.mushrooms.stub.config;

import com.github.wenhao.mushrooms.stub.dataloader.ResourceReader;
import com.github.wenhao.mushrooms.stub.domain.Stub;
import static java.util.stream.Collectors.toList;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Optional;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class StubConfiguration {

    private boolean enabled;
    private boolean failover;
    private List<Stub> stubs;

    public static StubConfiguration.StubConfigurationBuilder builder() {
        return new StubConfiguration.StubConfigurationBuilder();
    }

    public static class StubConfigurationBuilder {
        private boolean enabled;
        private boolean failover;
        private List<Stub> stubs;

        StubConfigurationBuilder() {
        }

        public StubConfiguration.StubConfigurationBuilder enabled(boolean enabled) {
            this.enabled = enabled;
            return this;
        }

        public StubConfiguration.StubConfigurationBuilder failover(boolean failover) {
            this.failover = failover;
            return this;
        }

        public StubConfiguration.StubConfigurationBuilder stubs(List<Stub> stubs) {
            this.stubs = stubs;
            return this;
        }

        public StubConfiguration build() {
            ResourceReader resourceReader = new ResourceReader();
            this.stubs = stubs.stream().peek(stub -> {
                final String body = Optional.ofNullable(stub.getRequest().getBody()).orElse("");
                if (!body.startsWith("xpath:") && !body.startsWith("jsonPath:")) {
                    stub.getRequest().setBody(resourceReader.readAsString(body));
                }
                stub.setResponse(Optional.ofNullable(stub.getResponse()).map(resourceReader::readAsString).orElse(""));
            }).collect(toList());
            return new StubConfiguration(this.enabled, this.failover, this.stubs);
        }
    }
}

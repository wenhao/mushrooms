/*
 * Copyright © 2019, Wen Hao <wenhao@126.com>.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

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

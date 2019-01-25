package com.github.wenhao.mushrooms.integration.config;

import com.github.wenhao.mushrooms.stub.config.StubConfiguration;
import com.github.wenhao.mushrooms.stub.okhttp.interceptor.StubOkHttpClientInterceptor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConditionalOnProperty(prefix = "mushrooms.stub", name = "enabled", havingValue = "true")
public class BeanConfiguration {

    @Bean
    public StubOkHttpClientInterceptor stubOkHttpClientInterceptor(MushroomsStubConfiguration properties) {
        StubConfiguration configuration = StubConfiguration.builder()
                .enabled(properties.isEnabled())
                .failover(properties.isFailover())
                .stubs(properties.getStubs())
                .build();
        return StubOkHttpClientInterceptor.builder().configuration(configuration).build();
    }

}

package com.github.wenhao.stub.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.wenhao.stub.dataloader.ResourceReader;
import com.github.wenhao.stub.dataloader.StubResponseDataLoader;
import com.github.wenhao.stub.matcher.BodyMatcher;
import com.github.wenhao.stub.matcher.JsonMatcher;
import com.github.wenhao.stub.okhttp.interceptor.StubOkHttpClientInterceptor;
import com.github.wenhao.stub.properties.MushroomsStubConfigurationProperties;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.core.io.ResourceLoader;

@Configuration
@ConditionalOnProperty(prefix = "mushrooms.stub", name = "enabled", havingValue = "true")
public class MushroomsStubAutoConfiguration {

    @Bean
    @Order(10)
    public StubOkHttpClientInterceptor stubOkHttpClientInterceptor(MushroomsStubConfigurationProperties properties,
                                                                   StubResponseDataLoader dataLoader,
                                                                   ResourceReader resourceReader,
                                                                   BodyMatcher bodyMatcher) {
        return new StubOkHttpClientInterceptor(properties, dataLoader, resourceReader, bodyMatcher);
    }


    @Bean
    public MushroomsStubConfigurationProperties mushroomsStubConfigurationProperties() {
        return new MushroomsStubConfigurationProperties();
    }

    @Bean
    public StubResponseDataLoader stubResponseDateLoader(ResourceReader resourceReader, BodyMatcher bodyMatcher) {
        return new StubResponseDataLoader(resourceReader, bodyMatcher);
    }

    @Bean
    public ResourceReader resourceReader(ResourceLoader resourceLoader) {
        return new ResourceReader(resourceLoader);
    }

    @Bean
    public BodyMatcher bodyMatcher(JsonMatcher jsonMatcher) {
        return new BodyMatcher(jsonMatcher);
    }

    @Bean
    public JsonMatcher jsonMatcher(ObjectMapper objectMapper) {
        return new JsonMatcher(objectMapper);
    }
}

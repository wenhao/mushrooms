package com.github.wenhao.stub.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.wenhao.stub.dataloader.DataLoader;
import com.github.wenhao.stub.dataloader.JsonDataLoader;
import com.github.wenhao.stub.okhttp.interceptor.StubOkHttpClientInterceptor;
import com.github.wenhao.stub.properties.MushroomsStubConfigurationProperties;
import com.github.wenhao.stub.utils.JsonMatcher;
import com.github.wenhao.stub.utils.ResourceReader;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.core.io.ResourceLoader;

import java.util.List;

@Configuration
@ConditionalOnExpression("${mushrooms.stub.okhttp.enabled:true} || ${mushrooms.stub.resttemplate.enabled:true}")
public class MushroomsStubAutoConfiguration {

    @Bean
    @Order(10)
    @ConditionalOnProperty(prefix = "mushrooms.stub.okhttp", name = "enabled", havingValue = "true")
    public StubOkHttpClientInterceptor stubOkHttpClientInterceptor(MushroomsStubConfigurationProperties properties,
                                                                   List<DataLoader> dataLoaders,
                                                                   ResourceReader resourceReader,
                                                                   JsonMatcher jsonMatcher) {
        return new StubOkHttpClientInterceptor(properties, dataLoaders, resourceReader, jsonMatcher);
    }


    @Bean
    public MushroomsStubConfigurationProperties mushroomsStubConfigurationProperties() {
        return new MushroomsStubConfigurationProperties();
    }

    @Bean
    public JsonDataLoader jsonDataLoader(ResourceReader resourceReader) {
        return new JsonDataLoader(resourceReader);
    }

    @Bean
    public ResourceReader resourceReader(ResourceLoader resourceLoader) {
        return new ResourceReader(resourceLoader);
    }

    @Bean
    public JsonMatcher jsonMatcher(ObjectMapper objectMapper) {
        return new JsonMatcher(objectMapper);
    }
}

package com.github.wenhao.stub.config;

import com.github.wenhao.stub.dataloader.DataLoader;
import com.github.wenhao.stub.dataloader.JsonDataLoader;
import com.github.wenhao.stub.okhttp.interceptor.StubOkHttpClientInterceptor;
import com.github.wenhao.stub.properties.MushroomsStubConfigurationProperties;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ResourceLoader;

import java.util.List;

@Configuration
@ConditionalOnExpression("${mushrooms.stub.okhttp.enabled:true} || ${mushrooms.stub.resttemplate.enabled:true}")
public class MushroomsStubAutoConfiguration {

    @Bean
    @ConditionalOnProperty(prefix = "mushrooms.stub.okhttp", name = "enabled", havingValue = "true")
    public StubOkHttpClientInterceptor stubOkHttpClientInterceptor(MushroomsStubConfigurationProperties properties,
                                                                   List<DataLoader> dataLoaders) {
        return new StubOkHttpClientInterceptor(properties, dataLoaders);
    }


    @Bean
    public MushroomsStubConfigurationProperties mushroomsStubConfigurationProperties() {
        return new MushroomsStubConfigurationProperties();
    }

    @Bean
    public JsonDataLoader jsonDataLoader(ResourceLoader resourceLoader) {
        return new JsonDataLoader(resourceLoader);
    }
}

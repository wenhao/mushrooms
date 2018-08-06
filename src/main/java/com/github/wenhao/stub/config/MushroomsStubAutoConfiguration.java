package com.github.wenhao.stub.config;

import com.github.wenhao.common.domain.Request;
import com.github.wenhao.common.domain.Response;
import com.github.wenhao.stub.dataloader.DataLoader;
import com.github.wenhao.stub.dataloader.JsonDataLoader;
import com.github.wenhao.stub.dataloader.StubDataLoader;
import com.github.wenhao.stub.okhttp.interceptor.StubOkHttpClientInterceptor;
import com.github.wenhao.stub.properties.MushroomsStubConfigurationProperties;
import com.github.wenhao.stub.repository.StubRepository;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ResourceLoader;
import org.springframework.data.redis.core.HashOperations;

import java.util.List;

@Configuration
@ConditionalOnExpression("${mushrooms.stub.okhttp.enabled:true} || ${mushrooms.stub.resttemplate.enabled:true}")
public class MushroomsStubAutoConfiguration {

    @Bean
    @ConditionalOnProperty(prefix = "mushrooms.stub.okhttp", name = "enabled", havingValue = "true")
    public StubOkHttpClientInterceptor stubOkHttpClientInterceptor(StubRepository repository,
                                                                   MushroomsStubConfigurationProperties properties) {
        return new StubOkHttpClientInterceptor(repository, properties);
    }

    @Bean
    public StubRepository stubRepository(HashOperations<String, Request, Response> stubHashOperations,
                                         MushroomsStubConfigurationProperties properties) {
        return new StubRepository(stubHashOperations, properties);
    }

    @Bean
    public MushroomsStubConfigurationProperties mushroomsStubConfigurationProperties() {
        return new MushroomsStubConfigurationProperties();
    }

    @Bean
    public StubDataLoader stubDataLoader(MushroomsStubConfigurationProperties properties, List<DataLoader> dataLoaders) {
        return new StubDataLoader(properties, dataLoaders);
    }

    @Bean
    public JsonDataLoader jsonDataLoader(ResourceLoader resourceLoader, StubRepository repository) {
        return new JsonDataLoader(resourceLoader, repository);
    }
}

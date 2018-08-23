package com.github.wenhao.stub.config;

import com.github.wenhao.stub.dataloader.ResourceReader;
import com.github.wenhao.stub.matcher.BodyMatcher;
import com.github.wenhao.stub.matcher.HeaderMatcher;
import com.github.wenhao.stub.matcher.JsonBodyMatcher;
import com.github.wenhao.stub.matcher.MethodMatcher;
import com.github.wenhao.stub.matcher.ParameterMatcher;
import com.github.wenhao.stub.matcher.PathMatcher;
import com.github.wenhao.stub.matcher.RequestBodyMatcher;
import com.github.wenhao.stub.matcher.RequestMatcher;
import com.github.wenhao.stub.matcher.XMLBodyMatcher;
import com.github.wenhao.stub.okhttp.interceptor.StubOkHttpClientInterceptor;
import com.github.wenhao.stub.properties.MushroomsStubConfigurationProperties;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.core.io.ResourceLoader;

import java.util.List;

@Configuration
@ConditionalOnProperty(prefix = "mushrooms.stub", name = "enabled", havingValue = "true")
public class MushroomsStubAutoConfiguration {

    @Bean
    @Order(10)
    public StubOkHttpClientInterceptor stubOkHttpClientInterceptor(MushroomsStubConfigurationProperties properties,
                                                                   List<RequestMatcher> requestMatchers) {
        return new StubOkHttpClientInterceptor(properties, requestMatchers);
    }


    @Bean
    public MushroomsStubConfigurationProperties mushroomsStubConfigurationProperties(ResourceReader resourceReader) {
        return new MushroomsStubConfigurationProperties(resourceReader);
    }

    @Bean
    public ResourceReader resourceReader(ResourceLoader resourceLoader) {
        return new ResourceReader(resourceLoader);
    }

    @Bean
    @Order(5)
    public RequestMatcher pathMatcher() {
        return new PathMatcher();
    }

    @Bean
    @Order(10)
    public RequestMatcher parameterMatcher() {
        return new ParameterMatcher();
    }

    @Bean
    @Order(15)
    public RequestMatcher methodMatcher() {
        return new MethodMatcher();
    }

    @Bean
    @Order(20)
    public RequestMatcher headerMatcher() {
        return new HeaderMatcher();
    }

    @Bean
    public BodyMatcher bodyMatcher(List<RequestBodyMatcher> requestBodyMatchers) {
        return new BodyMatcher(requestBodyMatchers);
    }

    @Bean
    public RequestBodyMatcher jsonBodyMatcher() {
        return new JsonBodyMatcher();
    }

    @Bean
    public RequestBodyMatcher xmlBodyMatcher() {
        return new XMLBodyMatcher();
    }

}

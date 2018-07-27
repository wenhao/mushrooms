package com.github.wenhao.okhttp.config;

import feign.Client;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
@ConditionalOnProperty(value = "mushrooms.okhttp.enabled", havingValue = "true")
public class CachingOkHttpClientConfiguration {

    @Autowired
    private List<Interceptor> interceptors;

    @Bean
    public Client feignClient(OkHttpClient okHttpClient) {
        final OkHttpClient.Builder builder = okHttpClient.newBuilder();
        interceptors.forEach(builder::addInterceptor);
        return new feign.okhttp.OkHttpClient(builder.build());
    }
}

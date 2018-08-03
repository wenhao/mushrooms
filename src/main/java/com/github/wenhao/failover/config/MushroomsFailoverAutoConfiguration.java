package com.github.wenhao.failover.config;

import com.github.wenhao.common.domain.Request;
import com.github.wenhao.common.domain.Response;
import com.github.wenhao.failover.okhttp.health.HttpStatusOkHttpClientHealthCheck;
import com.github.wenhao.failover.okhttp.health.OkHttpClientHealthCheck;
import com.github.wenhao.failover.okhttp.interceptor.CachingOkHttpClientInterceptor;
import com.github.wenhao.failover.properties.MushroomsFailoverConfigurationProperties;
import com.github.wenhao.failover.repository.FailoverRepository;
import com.github.wenhao.failover.resttemplate.config.CachingRestTemplatePostProcessor;
import com.github.wenhao.failover.resttemplate.health.HttpStatusRestTemplateHealthCheck;
import com.github.wenhao.failover.resttemplate.health.RestTemplateHealthCheck;
import com.github.wenhao.failover.resttemplate.interceptor.CachingRestTemplateInterceptor;
import feign.Client;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.util.List;

@Configuration
@ConditionalOnExpression("${mushrooms.failover.okhttp.enabled:true} || ${mushrooms.failover.resttemplate.enabled:true}")
public class MushroomsFailoverAutoConfiguration {

    @Bean
    @ConditionalOnProperty(prefix = "mushrooms.failover.okhttp", name = "enabled", havingValue = "true")
    public CachingOkHttpClientInterceptor cachingOkHttpClientInterceptor(FailoverRepository repository,
                                                                         MushroomsFailoverConfigurationProperties properties,
                                                                         List<OkHttpClientHealthCheck> healthChecks) {
        return new CachingOkHttpClientInterceptor(repository, properties, healthChecks);
    }

    @Bean
    @ConditionalOnProperty(prefix = "mushrooms.failover.okhttp", name = "enabled", havingValue = "true")
    public Client feignClient(OkHttpClient okHttpClient, List<Interceptor> interceptors) {
        final OkHttpClient.Builder builder = okHttpClient.newBuilder();
        interceptors.forEach(builder::addInterceptor);
        return new feign.okhttp.OkHttpClient(builder.build());
    }

    @Bean
    @ConditionalOnProperty(prefix = "mushrooms.failover.okhttp", name = "enabled", havingValue = "true")
    public HttpStatusOkHttpClientHealthCheck httpStatusOkHttpClientHealthCheck() {
        return new HttpStatusOkHttpClientHealthCheck();
    }

    @Bean
    @ConditionalOnProperty(prefix = "mushrooms.failover.resttemplate", name = "enabled", havingValue = "true")
    public CachingRestTemplatePostProcessor cachingRestTemplatePostProcessor(CachingRestTemplateInterceptor interceptor) {
        return new CachingRestTemplatePostProcessor(interceptor);
    }

    @Bean
    @ConditionalOnProperty(prefix = "mushrooms.failover.resttemplate", name = "enabled", havingValue = "true")
    public CachingRestTemplateInterceptor cachingRestTemplateInterceptor(FailoverRepository repository,
                                                                         MushroomsFailoverConfigurationProperties properties,
                                                                         List<RestTemplateHealthCheck> healthChecks) {
        return new CachingRestTemplateInterceptor(repository, properties, healthChecks);
    }

    @Bean
    @ConditionalOnProperty(prefix = "mushrooms.failover.resttemplate", name = "enabled", havingValue = "true")
    public HttpStatusRestTemplateHealthCheck httpStatusRestTemplateHealthCheck() {
        return new HttpStatusRestTemplateHealthCheck();
    }

    @Bean("cachingHashOperations")
    public HashOperations<String, Request, Response> cacheHashOperations(RedisTemplate redisTemplate) {
        redisTemplate.setKeySerializer(new StringRedisSerializer());
        redisTemplate.setHashKeySerializer(new Jackson2JsonRedisSerializer<>(Request.class));
        redisTemplate.setHashValueSerializer(new Jackson2JsonRedisSerializer<>(Response.class));
        redisTemplate.afterPropertiesSet();
        return redisTemplate.opsForHash();
    }

    @Bean
    @ConditionalOnMissingBean
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory redisConnectionFactory) {
        RedisTemplate<String, Object> redisTemplate = new RedisTemplate<>();
        redisTemplate.setConnectionFactory(redisConnectionFactory);
        return redisTemplate;
    }

    @Bean
    public FailoverRepository failoverRepository(HashOperations<String, Request, Response> cacheHashOperations,
                                                 MushroomsFailoverConfigurationProperties properties) {
        return new FailoverRepository(cacheHashOperations, properties);
    }

    @Bean
    public MushroomsFailoverConfigurationProperties mushroomsFailoverConfigurationProperties() {
        return new MushroomsFailoverConfigurationProperties();
    }
}

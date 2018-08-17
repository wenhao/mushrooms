package com.github.wenhao.failover.config;

import com.github.wenhao.common.domain.Request;
import com.github.wenhao.common.domain.Response;
import com.github.wenhao.failover.okhttp.health.HttpStatusOkHttpClientHealthCheck;
import com.github.wenhao.failover.okhttp.health.OkHttpClientHealthCheck;
import com.github.wenhao.failover.okhttp.interceptor.CachingOkHttpClientInterceptor;
import com.github.wenhao.failover.properties.MushroomsFailoverConfigurationProperties;
import com.github.wenhao.failover.repository.FailoverRepository;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.util.List;

@Configuration
@ConditionalOnProperty(prefix = "mushrooms.failover", name = "enabled", havingValue = "true")
public class MushroomsFailoverAutoConfiguration {

    @Bean
    @Order
    public CachingOkHttpClientInterceptor cachingOkHttpClientInterceptor(FailoverRepository repository,
                                                                         MushroomsFailoverConfigurationProperties properties,
                                                                         List<OkHttpClientHealthCheck> healthChecks) {
        return new CachingOkHttpClientInterceptor(repository, properties, healthChecks);
    }

    @Bean
    @Order(10)
    public HttpStatusOkHttpClientHealthCheck httpStatusOkHttpClientHealthCheck() {
        return new HttpStatusOkHttpClientHealthCheck();
    }

    @Bean
    @ConditionalOnMissingBean
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory redisConnectionFactory) {
        RedisTemplate<String, Object> redisTemplate = new RedisTemplate<>();
        redisTemplate.setConnectionFactory(redisConnectionFactory);
        return redisTemplate;
    }

    @Bean
    public FailoverRepository failoverRepository(HashOperations<String, Request, Response> hashOperations,
                                                 MushroomsFailoverConfigurationProperties properties) {
        return new FailoverRepository(hashOperations, properties);
    }

    @Bean
    public MushroomsFailoverConfigurationProperties mushroomsFailoverConfigurationProperties() {
        return new MushroomsFailoverConfigurationProperties();
    }

    @Bean
    public HashOperations<String, Request, Response> hashOperations(RedisTemplate redisTemplate) {
        redisTemplate.setKeySerializer(new StringRedisSerializer());
        redisTemplate.setHashKeySerializer(new Jackson2JsonRedisSerializer<>(Request.class));
        redisTemplate.setHashValueSerializer(new Jackson2JsonRedisSerializer<>(Response.class));
        redisTemplate.afterPropertiesSet();
        return redisTemplate.opsForHash();
    }
}

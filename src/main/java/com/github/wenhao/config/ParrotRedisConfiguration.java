package com.github.wenhao.config;

import com.github.wenhao.domain.HttpRequest;
import com.github.wenhao.domain.HttpResponse;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;

@Configuration
@ConditionalOnProperty(value = "http.cache.enabled", havingValue = "true", matchIfMissing = true)
public class ParrotRedisConfiguration {
    @Bean
    public RedisTemplate<String, Object> cacheRedisTemplate(
            RedisConnectionFactory redisConnectionFactory) {
        RedisTemplate<String, Object> redisTemplate = new RedisTemplate<>();
        redisTemplate.setConnectionFactory(redisConnectionFactory);
        redisTemplate.setHashKeySerializer(new Jackson2JsonRedisSerializer<>(HttpRequest.class));
        redisTemplate.setHashValueSerializer(new Jackson2JsonRedisSerializer<>(HttpResponse.class));
        return redisTemplate;
    }

    @Bean
    public HashOperations<String, HttpRequest, HttpResponse> cacheHashOperations(
            RedisTemplate<String, Object> redisTemplate) {
        return redisTemplate.opsForHash();
    }
}

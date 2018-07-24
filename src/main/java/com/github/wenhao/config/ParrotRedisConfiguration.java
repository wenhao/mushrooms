package com.github.wenhao.config;

import com.github.wenhao.domain.CacheRequest;
import com.sun.deploy.net.HttpResponse;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Configuration
@ConditionalOnProperty(value = "parrot.enabled", havingValue = "true", matchIfMissing = true)
public class ParrotRedisConfiguration {
    @Bean
    public RedisTemplate<String, Object> parrotRedisTemplate(RedisConnectionFactory redisConnectionFactory) {
        RedisTemplate<String, Object> redisTemplate = new RedisTemplate<>();
        redisTemplate.setConnectionFactory(redisConnectionFactory);
        redisTemplate.setHashKeySerializer(new Jackson2JsonRedisSerializer<>(CacheRequest.class));
        redisTemplate.setHashValueSerializer(new StringRedisSerializer());
        return redisTemplate;
    }

    @Bean
    public HashOperations<String, CacheRequest, String> parrotHashOperations(
            RedisTemplate<String, Object> redisTemplate) {
        return redisTemplate.opsForHash();
    }
}

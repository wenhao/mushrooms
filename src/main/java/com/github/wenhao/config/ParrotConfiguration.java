package com.github.wenhao.config;

import com.github.wenhao.domain.CacheRequest;
import lombok.Data;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import javax.annotation.PostConstruct;
import java.util.List;

import static java.util.Objects.isNull;

@Data
@Configuration
@ConfigurationProperties("parrot")
@ConditionalOnProperty(value = "parrot.enabled", havingValue = "true", matchIfMissing = true)
public class ParrotConfiguration {
    private String mapping;
    private String key;
    private List<String> headers;

    @PostConstruct
    public void init() {
        if (isNull(key)) {
            key = "PARROT-CACHE";
        }
    }

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

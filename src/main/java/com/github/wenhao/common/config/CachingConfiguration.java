package com.github.wenhao.common.config;

import com.github.wenhao.common.domain.Request;
import lombok.Data;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import javax.annotation.PostConstruct;
import java.util.List;

import static java.util.Objects.isNull;

@Data
@Configuration
@ConfigurationProperties("parrot")
@ConditionalOnExpression("${parrot.resttemplate.enabled:true} || ${parrot.okhttp.enabled:true}")
@ConditionalOnClass(RedisOperations.class)
public class CachingConfiguration {
    private String key;
    private List<String> headers;

    @PostConstruct
    public void init() {
        if (isNull(key)) {
            key = "PARROT-CACHE";
        }
    }

    @Bean
    public HashOperations<String, Request, String> cacheHashOperations(RedisConnectionFactory redisConnectionFactory) {
        RedisTemplate<String, Object> redisTemplate = new RedisTemplate<>();
        redisTemplate.setConnectionFactory(redisConnectionFactory);
        redisTemplate.setHashKeySerializer(new Jackson2JsonRedisSerializer<>(Request.class));
        redisTemplate.setHashValueSerializer(new StringRedisSerializer());
        return redisTemplate.opsForHash();
    }
}

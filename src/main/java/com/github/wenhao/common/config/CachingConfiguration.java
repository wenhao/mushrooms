package com.github.wenhao.common.config;

import com.github.wenhao.common.domain.Request;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Configuration
@ConditionalOnExpression("${parrot.resttemplate.enabled:true} || ${parrot.okhttp.enabled:true}")
public class CachingConfiguration {

    @Bean("cachingHashOperations")
    public HashOperations<String, Request, String> cacheHashOperations(RedisConnectionFactory redisConnectionFactory) {
        RedisTemplate<String, Object> redisTemplate = new RedisTemplate<>();
        redisTemplate.setConnectionFactory(redisConnectionFactory);
        redisTemplate.setKeySerializer(new StringRedisSerializer());
        redisTemplate.setHashKeySerializer(new Jackson2JsonRedisSerializer<>(Request.class));
        redisTemplate.setHashValueSerializer(new StringRedisSerializer());
        redisTemplate.afterPropertiesSet();
        return redisTemplate.opsForHash();
    }
}

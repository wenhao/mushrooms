package com.github.wenhao.common.config;

import com.github.wenhao.common.domain.Request;
import com.github.wenhao.common.domain.Response;
import feign.Client;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.util.List;

@Configuration
@ConditionalOnExpression("${mushrooms.failover.okhttp.enabled:true} || ${mushrooms.failover.resttemplate.enabled:true}")
public class MushroomsAutoConfiguration {

    @Bean
    @ConditionalOnExpression("${mushrooms.failover.okhttp.enabled:true}")
    public Client feignClient(OkHttpClient okHttpClient, List<Interceptor> interceptors) {
        final OkHttpClient.Builder builder = okHttpClient.newBuilder();
        interceptors.forEach(builder::addInterceptor);
        return new feign.okhttp.OkHttpClient(builder.build());
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

package com.github.wenhao.repository;

import com.github.wenhao.config.ParrotConfiguration;
import com.github.wenhao.domain.CacheRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
@ConditionalOnProperty(value = "parrot.enabled", havingValue = "true", matchIfMissing = true)
public class ParrotCacheRepository {
    private final HashOperations<String, CacheRequest, String> parrotHashOperations;
    private final ParrotConfiguration parrotConfiguration;

    public void save(CacheRequest request, String responseBody) {
        parrotHashOperations.put(parrotConfiguration.getKey(), request, responseBody);
    }

    public String get(CacheRequest request) {
        return parrotHashOperations.get(parrotConfiguration.getKey(), request);
    }

}

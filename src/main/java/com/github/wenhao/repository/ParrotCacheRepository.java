package com.github.wenhao.repository;

import com.github.wenhao.config.ParrotConfiguration;
import com.github.wenhao.domain.CacheRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
@ConditionalOnProperty(value = "parrot.enabled", havingValue = "true", matchIfMissing = true)
@Slf4j
public class ParrotCacheRepository {
    private final HashOperations<String, CacheRequest, String> parrotHashOperations;
    private final ParrotConfiguration parrotConfiguration;

    public void save(CacheRequest request, String responseBody) {
        parrotHashOperations.put(parrotConfiguration.getKey(), request, responseBody);
        log.info("[Parrot]Refresh cached data.");
    }

    public String get(CacheRequest request) {
        log.info("[Parrot]Respond with cached data.");
        return parrotHashOperations.get(parrotConfiguration.getKey(), request);
    }

}

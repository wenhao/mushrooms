package com.github.wenhao.failover.repository;

import com.github.wenhao.common.domain.Request;
import com.github.wenhao.failover.properties.MushroomsFailoverConfigurationProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.HashOperations;

@RequiredArgsConstructor
public class FailoverRepository {

    private final HashOperations<String, Request, String> cachingHashOperations;
    private final MushroomsFailoverConfigurationProperties mushroomsFailoverConfigurationProperties;

    public void save(Request request, String responseBody) {
        cachingHashOperations.put(mushroomsFailoverConfigurationProperties.getKey(), request, responseBody);
    }

    public String get(Request request) {
        return cachingHashOperations.get(mushroomsFailoverConfigurationProperties.getKey(), request);
    }

}

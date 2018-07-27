package com.github.wenhao.common.repository;

import com.github.wenhao.common.config.CachingConfigurationProperties;
import com.github.wenhao.common.domain.Request;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.stereotype.Repository;

@Repository
@ConditionalOnExpression("${parrot.resttemplate.enabled:true} || ${parrot.okhttp.enabled:true}")
public class CachingRepository {

    @Autowired
    @Qualifier("cachingHashOperations")
    private HashOperations<String, Request, String> cachingHashOperations;
    @Autowired
    private CachingConfigurationProperties cachingConfigurationProperties;

    public void save(Request request, String responseBody) {
        cachingHashOperations.put(cachingConfigurationProperties.getKey(), request, responseBody);
    }

    public String get(Request request) {
        return cachingHashOperations.get(cachingConfigurationProperties.getKey(), request);
    }

}

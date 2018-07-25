package com.github.wenhao.common.repository;

import com.github.wenhao.common.config.CachingConfiguration;
import com.github.wenhao.common.domain.Request;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.stereotype.Repository;

@Slf4j
@Repository
@ConditionalOnExpression("${parrot.resttemplate.enabled:true} || ${parrot.okhttp.enabled:true}")
public class CachingRepository {

    @Autowired
    @Qualifier("cachingHashOperations")
    private HashOperations<String, Request, String> cachingHashOperations;
    @Autowired
    private CachingConfiguration cachingConfiguration;

    public void save(Request request, String responseBody) {
        cachingHashOperations.put(cachingConfiguration.getKey(), request, responseBody);
        log.info("[Parrot]Refresh cached data.");
    }

    public String get(Request request) {
        log.info("[Parrot]Respond with cached data.");
        return cachingHashOperations.get(cachingConfiguration.getKey(), request);
    }

}

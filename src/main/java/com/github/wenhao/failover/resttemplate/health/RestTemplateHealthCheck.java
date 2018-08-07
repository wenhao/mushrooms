package com.github.wenhao.failover.resttemplate.health;

import com.github.wenhao.failover.resttemplate.response.ClientHttpResponseWrapper;

@FunctionalInterface
public interface RestTemplateHealthCheck {

    boolean health(ClientHttpResponseWrapper response);
}

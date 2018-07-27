package com.github.wenhao.failover.resttemplate.health;

import com.github.wenhao.failover.resttemplate.interceptor.ClientHttpResponseWrapper;

public interface RestTemplateHealthCheck {
    boolean health(ClientHttpResponseWrapper response);
}

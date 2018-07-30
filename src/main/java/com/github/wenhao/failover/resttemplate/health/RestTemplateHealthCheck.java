package com.github.wenhao.failover.resttemplate.health;

import com.github.wenhao.failover.resttemplate.response.ClientHttpResponseWrapper;

public interface RestTemplateHealthCheck {
    boolean health(ClientHttpResponseWrapper response);
}

package com.github.wenhao.resttemplate.health;

import com.github.wenhao.resttemplate.interceptor.ClientHttpResponseWrapper;

public interface RestTemplateHealthCheck {
    boolean health(ClientHttpResponseWrapper response);
}

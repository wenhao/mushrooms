package com.github.wenhao.health;

import com.github.wenhao.interceptor.ClientHttpResponseWrapper;

public interface RestTemplateHealthCheck {
    boolean health(ClientHttpResponseWrapper response);
}

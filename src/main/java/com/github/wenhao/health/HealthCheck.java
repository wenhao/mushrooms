package com.github.wenhao.health;

import com.github.wenhao.interceptor.ClientHttpResponseWrapper;

public interface HealthCheck {
    boolean health(ClientHttpResponseWrapper response);
}

package com.github.wenhao.health;

import org.springframework.http.client.ClientHttpResponse;

public interface HealthCheck {
    boolean health(ClientHttpResponse response);
}

package com.github.wenhao.failover.resttemplate.health;

import com.github.wenhao.failover.resttemplate.response.ClientHttpResponseWrapper;

import java.io.IOException;

import static org.springframework.http.HttpStatus.OK;

public class HttpStatusRestTemplateHealthCheck implements RestTemplateHealthCheck {

    @Override
    public boolean health(final ClientHttpResponseWrapper response) {
        try {
            return OK.equals(response.getStatusCode());
        } catch (IOException e) {
            return false;
        }
    }
}

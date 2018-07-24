package com.github.wenhao.health;

import org.springframework.http.client.ClientHttpResponse;
import org.springframework.stereotype.Component;

import java.io.IOException;

import static org.springframework.http.HttpStatus.OK;

@Component
public class HttpStatusHealthCheck implements HealthCheck {

    @Override
    public boolean health(final ClientHttpResponse response) {
        try {
            return OK.equals(response.getStatusCode());
        } catch (IOException e) {
            return false;
        }
    }
}

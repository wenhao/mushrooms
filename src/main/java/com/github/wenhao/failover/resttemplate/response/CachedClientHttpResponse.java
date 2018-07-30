package com.github.wenhao.failover.resttemplate.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.client.ClientHttpResponse;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

import static java.util.Objects.nonNull;

@RequiredArgsConstructor
@AllArgsConstructor
@Builder
public class CachedClientHttpResponse implements ClientHttpResponse {

    private HttpStatus httpStatus;
    private HttpHeaders httpHeaders;
    private String body;
    private ClientHttpResponse response;

    public CachedClientHttpResponse(final ClientHttpResponse response) {
        this.response = response;
    }

    @Override
    public HttpStatus getStatusCode() throws IOException {
        if (nonNull(response)) {
            return this.response.getStatusCode();
        }
        return this.httpStatus;
    }

    @Override
    public int getRawStatusCode() throws IOException {
        if (nonNull(response)) {
            return this.response.getRawStatusCode();
        }
        return this.httpStatus.value();
    }

    @Override
    public String getStatusText() throws IOException {
        if (nonNull(response)) {
            return this.response.getStatusText();
        }
        return this.httpStatus.getReasonPhrase();
    }

    @Override
    public void close() {
        if (nonNull(response)) {
            this.response.close();
        }
    }

    @Override
    public InputStream getBody() throws IOException {
        if (nonNull(response)) {
            return this.response.getBody();
        }
        return new ByteArrayInputStream(this.body.getBytes());
    }

    @Override
    public HttpHeaders getHeaders() {
        if (nonNull(response)) {
            return this.response.getHeaders();
        }
        return this.httpHeaders;
    }
}

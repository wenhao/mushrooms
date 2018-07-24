package com.github.wenhao.interceptor;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.util.StreamUtils;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import static java.util.stream.Collectors.joining;

public class ClientHttpResponseWrapper implements ClientHttpResponse {
    private final ClientHttpResponse response;

    private byte[] body;


    ClientHttpResponseWrapper(ClientHttpResponse response) {
        this.response = response;
    }

    public HttpStatus getStatusCode() throws IOException {
        return this.response.getStatusCode();
    }

    public int getRawStatusCode() throws IOException {
        return this.response.getRawStatusCode();
    }

    public String getStatusText() throws IOException {
        return this.response.getStatusText();
    }

    public HttpHeaders getHeaders() {
        return this.response.getHeaders();
    }

    public InputStream getBody() throws IOException {
        if (this.body == null) {
            this.body = StreamUtils.copyToByteArray(this.response.getBody());
        }
        return new ByteArrayInputStream(this.body);
    }

    public String getBodyAsString() {
        try {
            return new BufferedReader(new InputStreamReader(getBody())).lines()
                    .parallel().collect(joining("\n"));
        } catch (IOException e) {
            return "";
        }
    }

    public void close() {
        this.response.close();
    }
}

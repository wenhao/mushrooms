package com.github.wenhao.failover.okhttp.health;

import com.github.wenhao.stub.okhttp.health.HttpStatusOkHttpClientHealthCheck;
import okhttp3.Request;
import okhttp3.Response;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static okhttp3.Protocol.HTTP_1_1;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.OK;

class HttpStatusOkHttpClientHealthCheckTest {

    private HttpStatusOkHttpClientHealthCheck healthCheck;

    @BeforeEach
    void setUp() {
        healthCheck = new HttpStatusOkHttpClientHealthCheck();
    }

    @Test
    void should_return_true_when_response_success() {
        // given
        final Response response = new Response.Builder()
                .code(OK.value())
                .request(new Request.Builder().url("http://integration.com").build())
                .protocol(HTTP_1_1)
                .message("message")
                .build();

        // when
        final boolean health = healthCheck.health(response);

        // then
        assertThat(health).isTrue();
    }

    @Test
    void should_return_false_when_response_failure() {
        // given
        final Response response = new Response.Builder()
                .code(BAD_REQUEST.value())
                .request(new Request.Builder().url("http://integration.com").build())
                .protocol(HTTP_1_1)
                .message("message")
                .build();

        // when
        final boolean health = healthCheck.health(response);

        // then
        assertThat(health).isFalse();

    }
}
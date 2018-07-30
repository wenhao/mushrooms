package com.github.wenhao.failover.resttemplate.health;

import com.github.wenhao.failover.resttemplate.response.ClientHttpResponseWrapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.http.HttpStatus.OK;

class HttpStatusRestTemplateHealthCheckTest {

    private HttpStatusRestTemplateHealthCheck healthCheck;

    @BeforeEach
    void setUp() {
        healthCheck = new HttpStatusRestTemplateHealthCheck();
    }

    @Test
    void should_return_true_when_response_success() throws IOException {
        // given
        final ClientHttpResponseWrapper response = mock(ClientHttpResponseWrapper.class);

        // when
        when(response.getStatusCode()).thenReturn(OK);
        final boolean health = healthCheck.health(response);

        // then
        assertThat(health).isTrue();
    }

    @Test
    void should_return_false_when_response_failure() throws IOException {
        // given
        final ClientHttpResponseWrapper response = mock(ClientHttpResponseWrapper.class);

        // when
        given(response.getStatusCode()).willThrow(new IOException());
        final boolean health = healthCheck.health(response);

        // then
        assertThat(health).isFalse();
    }
}
/*
 * Copyright © 2019, Wen Hao <wenhao@126.com>.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package com.github.wenhao.mushrooms.stub.okhttp.health;

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
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

package com.github.wenhao.mushrooms.stub.matcher;

import com.github.wenhao.mushrooms.stub.domain.Request;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.net.URISyntaxException;

import static org.assertj.core.api.Assertions.assertThat;

class PathMatcherTest {

    private PathMatcher pathMatcher;

    @BeforeEach
    void setUp() {
        pathMatcher = new PathMatcher();
    }

    @Test
    void should_match_url_path_without_regex() throws URISyntaxException {
        // given
        final Request stub = Request.builder()
                .path("http://127.0.0.1:8080/test-path")
                .build();
        final Request real = Request.builder()
                .path("http://127.0.0.1:8080/test-path")
                .build();

        // when
        final boolean isMatch = pathMatcher.match(real, stub);

        // then
        assertThat(isMatch).isTrue();
    }

    @Test
    void should_match_url_path_by_using_regex() throws URISyntaxException {
        // given
        final Request stub = Request.builder()
                .path("http://127.0.0.1:8080/test-path/a10003")
                .build();
        final Request real = Request.builder()
                .path("http://127.0.0.1:8080/test(.*)/([a-z]{1}[0-9]{5})")
                .build();

        // when
        final boolean isMatch = pathMatcher.match(real, stub);

        // then
        assertThat(isMatch).isTrue();
    }

    @Test
    void should_not_match_url_path_if_regex_not_match() throws URISyntaxException {
        // given
        final Request stub = Request.builder()
                .path("http://127.0.0.1:8080/test-path/a10003")
                .build();
        final Request real = Request.builder()
                .path("http://127.0.0.1:8080/test(.*)/([a-z]{1}[0-9]{3})")
                .build();

        // when
        final boolean isMatch = pathMatcher.match(real, stub);

        // then
        assertThat(isMatch).isFalse();
    }
}
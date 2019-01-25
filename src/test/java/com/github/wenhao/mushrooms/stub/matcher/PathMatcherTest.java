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
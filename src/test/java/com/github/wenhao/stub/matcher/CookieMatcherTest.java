package com.github.wenhao.stub.matcher;

import com.github.wenhao.common.domain.Cookie;
import com.github.wenhao.common.domain.Request;
import com.google.common.collect.ImmutableList;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class CookieMatcherTest {

    private CookieMatcher cookieMatcher;

    @BeforeEach
    void setUp() {
        cookieMatcher = new CookieMatcher();
    }

    @Test
    void should_match_cookie() {
        // given
        final Request stub = Request.builder()
                .cookies(ImmutableList.of(Cookie.builder().name("key").value("value").build()))
                .build();
        final Request real = Request.builder()
                .cookies(ImmutableList.of(Cookie.builder().name("key").value("value").build()))
                .build();

        // when
        boolean isMatch = cookieMatcher.match(stub, real);

        // then
        assertThat(isMatch).isTrue();
    }

    @Test
    void should_not_match_if_cookie_key_not_match() {
        // given
        final Request stub = Request.builder()
                .cookies(ImmutableList.of(Cookie.builder().name("key").value("value").build()))
                .build();
        final Request real = Request.builder()
                .cookies(ImmutableList.of(Cookie.builder().name("key1").value("value").build()))
                .build();

        // when
        final boolean isMatch = cookieMatcher.match(stub, real);

        // then
        assertThat(isMatch).isFalse();
    }

    @Test
    void should_not_match_if_cookie_value_not_match() {
        // given
        final Request stub = Request.builder()
                .cookies(ImmutableList.of(Cookie.builder().name("key").value("value").build()))
                .build();
        final Request real = Request.builder()
                .cookies(ImmutableList.of(Cookie.builder().name("key").value("value1").build()))
                .build();

        // when
        final boolean isMatch = cookieMatcher.match(stub, real);

        // then
        assertThat(isMatch).isFalse();
    }

    @Test
    void should_match_cookie_by_using_regex() {
        // given
        final Request stub = Request.builder()
                .cookies(ImmutableList.of(Cookie.builder().name("[A-z]{0,10}").value("[A-Z0-9]+").build()))
                .build();
        final Request real = Request.builder()
                .cookies(ImmutableList.of(Cookie.builder().name("key").value("VALUE1").build()))
                .build();

        // when
        final boolean isMatch = cookieMatcher.match(stub, real);

        // then
        assertThat(isMatch).isTrue();
    }

    @Test
    void should_match_if_cookie_is_empty() {
        // given
        final Request stub = Request.builder()
                .build();
        final Request real = Request.builder()
                .cookies(ImmutableList.of(Cookie.builder().name("key").value("value").build()))
                .build();

        // when
        final boolean isMatch = cookieMatcher.match(stub, real);

        // then
        assertThat(isMatch).isTrue();

    }
}

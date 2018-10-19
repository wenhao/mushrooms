package com.github.wenhao.mushrooms.stub.matcher;

import com.github.wenhao.mushrooms.common.domain.Header;
import com.github.wenhao.mushrooms.common.domain.Request;
import com.google.common.collect.ImmutableList;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class HeaderMatcherTest {

    private HeaderMatcher headerMatcher;

    @BeforeEach
    void setUp() {
        headerMatcher = new HeaderMatcher();
    }

    @Test
    void should_match_headers() {
        // given
        final Request stub = Request.builder()
                .headers(ImmutableList.of(Header.builder().name("key").value("value").build()))
                .build();
        final Request real = Request.builder()
                .headers(ImmutableList.of(Header.builder().name("key").value("value").build()))
                .build();

        // when
        final boolean isMatch = headerMatcher.match(stub, real);

        // then
        assertThat(isMatch).isTrue();
    }

    @Test
    void should_not_match_if_header_key_not_match() {
        // given
        final Request stub = Request.builder()
                .headers(ImmutableList.of(Header.builder().name("key").value("value").build()))
                .build();
        final Request real = Request.builder()
                .headers(ImmutableList.of(Header.builder().name("key1").value("value").build()))
                .build();

        // when
        final boolean isMatch = headerMatcher.match(stub, real);

        // then
        assertThat(isMatch).isFalse();
    }

    @Test
    void should_not_match_if_header_value_not_match() {
        // given
        final Request stub = Request.builder()
                .headers(ImmutableList.of(Header.builder().name("key").value("value").build()))
                .build();
        final Request real = Request.builder()
                .headers(ImmutableList.of(Header.builder().name("key").value("value1").build()))
                .build();

        // when
        final boolean isMatch = headerMatcher.match(stub, real);

        // then
        assertThat(isMatch).isFalse();
    }

    @Test
    void should_match_header_by_using_regex() {
        // given
        final Request stub = Request.builder()
                .headers(ImmutableList.of(Header.builder().name("[A-z]{0,10}").value("[A-Z0-9]+").build()))
                .build();
        final Request real = Request.builder()
                .headers(ImmutableList.of(Header.builder().name("key").value("VALUE1").build()))
                .build();

        // when
        final boolean isMatch = headerMatcher.match(stub, real);

        // then
        assertThat(isMatch).isTrue();
    }

    @Test
    void should_match_if_header_is_empty() {
        // given
        final Request stub = Request.builder()
                .build();
        final Request real = Request.builder()
                .headers(ImmutableList.of(Header.builder().name("key").value("value").build()))
                .build();

        // when
        final boolean isMatch = headerMatcher.match(stub, real);

        // then
        assertThat(isMatch).isTrue();

    }
}
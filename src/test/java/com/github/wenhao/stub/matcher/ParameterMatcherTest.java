package com.github.wenhao.stub.matcher;

import com.github.wenhao.common.domain.Parameter;
import com.github.wenhao.common.domain.Request;
import com.google.common.collect.ImmutableList;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ParameterMatcherTest {

    private ParameterMatcher parameterMatcher;

    @BeforeEach
    void setUp() {
        parameterMatcher = new ParameterMatcher();
    }

    @Test
    void should_match_parameters() {
        // given
        final Request stub = Request.builder()
                .parameters(ImmutableList.of(Parameter.builder().name("key").value("value").build()))
                .build();
        final Request real = Request.builder()
                .parameters(ImmutableList.of(Parameter.builder().name("key").value("value").build()))
                .build();

        // when
        final boolean isMatch = parameterMatcher.match(stub, real);

        // then
        assertThat(isMatch).isTrue();
    }

    @Test
    void should_not_match_if_parameters_key_not_match() {
        // given
        final Request stub = Request.builder()
                .parameters(ImmutableList.of(Parameter.builder().name("key").value("value").build()))
                .build();
        final Request real = Request.builder()
                .parameters(ImmutableList.of(Parameter.builder().name("key1").value("value").build()))
                .build();

        // when
        final boolean isMatch = parameterMatcher.match(stub, real);

        // then
        assertThat(isMatch).isFalse();
    }

    @Test
    void should_not_match_if_parameters_value_not_match() {
        // given
        final Request stub = Request.builder()
                .parameters(ImmutableList.of(Parameter.builder().name("key").value("value").build()))
                .build();
        final Request real = Request.builder()
                .parameters(ImmutableList.of(Parameter.builder().name("key").value("value1").build()))
                .build();

        // when
        final boolean isMatch = parameterMatcher.match(stub, real);

        // then
        assertThat(isMatch).isFalse();
    }

    @Test
    void should_match_parameters_by_using_regex() {
        // given
        final Request stub = Request.builder()
                .parameters(ImmutableList.of(Parameter.builder().name("[A-z]{0,10}").value("[A-Z0-9]+").build()))
                .build();
        final Request real = Request.builder()
                .parameters(ImmutableList.of(Parameter.builder().name("key").value("VALUE1").build()))
                .build();

        // when
        final boolean isMatch = parameterMatcher.match(stub, real);

        // then
        assertThat(isMatch).isTrue();
    }

    @Test
    void should_match_if_parameter_is_empty() {
        // given
        final Request stub = Request.builder()
                .build();
        final Request real = Request.builder()
                .parameters(ImmutableList.of(Parameter.builder().name("key").value("value").build()))
                .build();

        // when
        final boolean isMatch = parameterMatcher.match(stub, real);

        // then
        assertThat(isMatch).isTrue();
    }
}
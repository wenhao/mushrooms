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

import com.github.wenhao.mushrooms.stub.domain.Parameter;
import com.github.wenhao.mushrooms.stub.domain.Request;
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
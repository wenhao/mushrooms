package com.github.wenhao.mushrooms.stub.matcher;

import com.github.wenhao.mushrooms.stub.domain.Request;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class MethodMatcherTest {

    private MethodMatcher methodMatcher;

    @BeforeEach
    void setUp() {
        methodMatcher = new MethodMatcher();
    }

    @Test
    void should_match_method() {
        // given
        final Request stub = Request.builder()
                .method("P.*")
                .build();
        final Request real = Request.builder()
                .method("POST")
                .build();

        // when
        final boolean isMatch = methodMatcher.match(stub, real);

        // then
        assertThat(isMatch).isTrue();
    }
}

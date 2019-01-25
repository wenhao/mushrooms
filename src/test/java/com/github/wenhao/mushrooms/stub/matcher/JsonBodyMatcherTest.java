package com.github.wenhao.mushrooms.stub.matcher;

import com.github.wenhao.mushrooms.stub.domain.Request;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class JsonBodyMatcherTest {

    private JsonBodyMatcher jsonBodyMatcher;

    @BeforeEach
    void setUp() {
        jsonBodyMatcher = new JsonBodyMatcher();
    }

    @Test
    void should_match_json() {
        // given
        final Request stub = Request.builder()
                .body("{\"id\":1,\"name\":\"Juergen\"}")
                .build();
        final Request real = Request.builder()
                .body("{\"name\":\"Juergen\",\"id\":1}")
                .build();

        // when
        final boolean isMatch = jsonBodyMatcher.match(stub, real);

        // then
        assertThat(isMatch).isTrue();
    }

    @Test
    void should_match_json_partially() {
        // given
        final Request stub = Request.builder()
                .body("{\"id\":1}")
                .build();
        final Request real = Request.builder()
                .body("{\"name\":\"Juergen\",\"id\":1}")
                .build();

        // when
        final boolean isMatch = jsonBodyMatcher.match(stub, real);

        // then
        assertThat(isMatch).isTrue();
    }

    @Test
    void should_not_match_if_body_not_json() {
        // given
        final Request stub = Request.builder()
                .body("<bookstore> \n" +
                        "   <book nationality=\"ITALIAN\" category=\"COOKING\">\n" +
                        "       <title lang=\"en\">Everyday Italian</title>\n" +
                        "       <author>Giada De Laurentiis</author>\n" +
                        "       <year>2005</year>\n" +
                        "       <price>30.00</price>\n" +
                        "   </book>\n" +
                        "</bookstore>")
                .build();
        final Request real = Request.builder()
                .body("{\"name\":\"Juergen\",\"id\":1}")
                .build();

        // when
        final boolean isMatch = jsonBodyMatcher.match(stub, real);

        // then
        assertThat(isMatch).isFalse();
    }

    @Test
    void should_applicable_if_json() {
        // given
        final Request real = Request.builder()
                .contentType("application/json")
                .build();

        // when
        final boolean isApplicable = jsonBodyMatcher.isApplicable(null, real);

        // then
        assertThat(isApplicable).isTrue();
    }

    @Test
    void should_not_applicable_if_not_json() {
        // given
        final Request real = Request.builder()
                .contentType("text/xml")
                .build();

        // when
        final boolean isApplicable = jsonBodyMatcher.isApplicable(null, real);

        // then
        assertThat(isApplicable).isFalse();
    }
}
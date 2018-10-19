package com.github.wenhao.mushrooms.stub.matcher;

import com.github.wenhao.mushrooms.common.domain.Request;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class JsonPathMatcherTest {

    private JsonPathMatcher jsonPathMatcher;

    @BeforeEach
    void setUp() {
        jsonPathMatcher = new JsonPathMatcher();
    }

    @Test
    void should_applicable() {
        // given
        final Request stub = Request.builder()
                .body("jsonPath:$.name")
                .build();
        final Request real = Request.builder()
                .body("{\"name\":\"Juergen\",\"id\":1}")
                .contentType("application/json")
                .build();

        // when
        final boolean applicable = jsonPathMatcher.isApplicable(stub, real);

        // then
        assertThat(applicable).isTrue();
    }

    @Test
    void should_not_applicable_content_type_not_json() {
        // given
        final Request stub = Request.builder()
                .body("jsonPath:$.name")
                .build();
        final Request real = Request.builder()
                .body("{\"name\":\"Juergen\",\"id\":1}")
                .contentType("text/xml")
                .build();

        // when
        final boolean applicable = jsonPathMatcher.isApplicable(stub, real);

        // then
        assertThat(applicable).isFalse();
    }

    @Test
    void should_not_applicable_not_json_path() {
        // given
        final Request stub = Request.builder()
                .body("{\"name\":\"Juergen\",\"id\":1}")
                .build();
        final Request real = Request.builder()
                .body("{\"name\":\"Juergen\",\"id\":1}")
                .contentType("application/json")
                .build();

        // when
        final boolean applicable = jsonPathMatcher.isApplicable(stub, real);

        // then
        assertThat(applicable).isFalse();
    }

    @Test
    void should_match_json_path() {
        // given
        final Request stub = Request.builder()
                .body("jsonPath:$.store.bicycle[?(@.price == '19.95')]")
                .build();
        final Request real = Request.builder()
                .body("{\n" +
                        "    \"store\": {\n" +
                        "        \"book\": [\n" +
                        "            {\n" +
                        "                \"category\": \"reference\",\n" +
                        "                \"author\": \"Nigel Rees\",\n" +
                        "                \"title\": \"Sayings of the Century\",\n" +
                        "                \"price\": 8.95\n" +
                        "            },\n" +
                        "            {\n" +
                        "                \"category\": \"fiction\",\n" +
                        "                \"author\": \"Evelyn Waugh\",\n" +
                        "                \"title\": \"Sword of Honour\",\n" +
                        "                \"price\": 12.99\n" +
                        "            },\n" +
                        "            {\n" +
                        "                \"category\": \"fiction\",\n" +
                        "                \"author\": \"Herman Melville\",\n" +
                        "                \"title\": \"Moby Dick\",\n" +
                        "                \"isbn\": \"0-553-21311-3\",\n" +
                        "                \"price\": 8.99\n" +
                        "            },\n" +
                        "            {\n" +
                        "                \"category\": \"fiction\",\n" +
                        "                \"author\": \"J. R. R. Tolkien\",\n" +
                        "                \"title\": \"The Lord of the Rings\",\n" +
                        "                \"isbn\": \"0-395-19395-8\",\n" +
                        "                \"price\": 22.99\n" +
                        "            }\n" +
                        "        ],\n" +
                        "        \"bicycle\": {\n" +
                        "            \"color\": \"red\",\n" +
                        "            \"price\": 19.95\n" +
                        "        }\n" +
                        "    },\n" +
                        "    \"expensive\": 10\n" +
                        "}")
                .contentType("application/json")
                .build();

        // when
        final boolean applicable = jsonPathMatcher.match(stub, real);

        // then
        assertThat(applicable).isTrue();
    }

    @Test
    void should_not_match_json_path() {
        // given
        final Request stub = Request.builder()
                .body("jsonPath:$.store.bicycle[?(@.price == '20.00')]")
                .build();
        final Request real = Request.builder()
                .body("{\n" +
                        "    \"store\": {\n" +
                        "        \"book\": [\n" +
                        "            {\n" +
                        "                \"category\": \"reference\",\n" +
                        "                \"author\": \"Nigel Rees\",\n" +
                        "                \"title\": \"Sayings of the Century\",\n" +
                        "                \"price\": 8.95\n" +
                        "            },\n" +
                        "            {\n" +
                        "                \"category\": \"fiction\",\n" +
                        "                \"author\": \"Evelyn Waugh\",\n" +
                        "                \"title\": \"Sword of Honour\",\n" +
                        "                \"price\": 12.99\n" +
                        "            },\n" +
                        "            {\n" +
                        "                \"category\": \"fiction\",\n" +
                        "                \"author\": \"Herman Melville\",\n" +
                        "                \"title\": \"Moby Dick\",\n" +
                        "                \"isbn\": \"0-553-21311-3\",\n" +
                        "                \"price\": 8.99\n" +
                        "            },\n" +
                        "            {\n" +
                        "                \"category\": \"fiction\",\n" +
                        "                \"author\": \"J. R. R. Tolkien\",\n" +
                        "                \"title\": \"The Lord of the Rings\",\n" +
                        "                \"isbn\": \"0-395-19395-8\",\n" +
                        "                \"price\": 22.99\n" +
                        "            }\n" +
                        "        ],\n" +
                        "        \"bicycle\": {\n" +
                        "            \"color\": \"red\",\n" +
                        "            \"price\": 19.95\n" +
                        "        }\n" +
                        "    },\n" +
                        "    \"expensive\": 10\n" +
                        "}")
                .contentType("application/json")
                .build();

        // when
        final boolean applicable = jsonPathMatcher.match(stub, real);

        // then
        assertThat(applicable).isFalse();
    }

    @Test
    void should_not_match_when_json_path_not_exist() {
        // given
        final Request stub = Request.builder()
                .body("jsonPath:$.store.not.exist[?(@.price == '20.00')]")
                .build();
        final Request real = Request.builder()
                .body("{\n" +
                        "    \"store\": {\n" +
                        "        \"book\": [\n" +
                        "            {\n" +
                        "                \"category\": \"reference\",\n" +
                        "                \"author\": \"Nigel Rees\",\n" +
                        "                \"title\": \"Sayings of the Century\",\n" +
                        "                \"price\": 8.95\n" +
                        "            },\n" +
                        "            {\n" +
                        "                \"category\": \"fiction\",\n" +
                        "                \"author\": \"Evelyn Waugh\",\n" +
                        "                \"title\": \"Sword of Honour\",\n" +
                        "                \"price\": 12.99\n" +
                        "            },\n" +
                        "            {\n" +
                        "                \"category\": \"fiction\",\n" +
                        "                \"author\": \"Herman Melville\",\n" +
                        "                \"title\": \"Moby Dick\",\n" +
                        "                \"isbn\": \"0-553-21311-3\",\n" +
                        "                \"price\": 8.99\n" +
                        "            },\n" +
                        "            {\n" +
                        "                \"category\": \"fiction\",\n" +
                        "                \"author\": \"J. R. R. Tolkien\",\n" +
                        "                \"title\": \"The Lord of the Rings\",\n" +
                        "                \"isbn\": \"0-395-19395-8\",\n" +
                        "                \"price\": 22.99\n" +
                        "            }\n" +
                        "        ],\n" +
                        "        \"bicycle\": {\n" +
                        "            \"color\": \"red\",\n" +
                        "            \"price\": 19.95\n" +
                        "        }\n" +
                        "    },\n" +
                        "    \"expensive\": 10\n" +
                        "}")
                .contentType("application/json")
                .build();

        // when
        final boolean applicable = jsonPathMatcher.match(stub, real);

        // then
        assertThat(applicable).isFalse();
    }
}
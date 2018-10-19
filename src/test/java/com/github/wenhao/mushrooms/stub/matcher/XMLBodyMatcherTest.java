package com.github.wenhao.mushrooms.stub.matcher;

import com.github.wenhao.mushrooms.common.domain.Request;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class XMLBodyMatcherTest {

    private XMLBodyMatcher xmlBodyMatcher;

    @BeforeEach
    void setUp() {
        xmlBodyMatcher = new XMLBodyMatcher();
    }

    @Test
    void should_match_xml() {
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
                .body("<bookstore> \n" +
                        "   <book nationality=\"ITALIAN\" category=\"COOKING\">\n" +
                        "       <title lang=\"en\">Everyday Italian</title>\n" +
                        "       <author>Giada De Laurentiis</author>\n" +
                        "       <year>2005</year>\n" +
                        "       <price>30.00</price>\n" +
                        "   </book>\n" +
                        "</bookstore>")
                .build();

        // when
        final boolean isMatch = xmlBodyMatcher.match(stub, real);

        // then
        assertThat(isMatch).isTrue();
    }

    @Test
    void should_match_xml_partially() {
        // given
        final Request stub = Request.builder()
                .body("<bookstore> \n" +
                        "   <book nationality=\"ITALIAN\" category=\"COOKING\">\n" +
                        "       <title lang=\"en\">Everyday Italian</title>\n" +
                        "       <year>2005</year>\n" +
                        "       <author>Giada De Laurentiis</author>\n" +
                        "   </book>\n" +
                        "</bookstore>")
                .build();
        final Request real = Request.builder()
                .body("<bookstore> \n" +
                        "   <book nationality=\"ITALIAN\" category=\"COOKING\">\n" +
                        "       <title lang=\"en\">Everyday Italian</title>\n" +
                        "       <author>Giada De Laurentiis</author>\n" +
                        "       <year>2005</year>\n" +
                        "       <price>30.00</price>\n" +
                        "   </book>\n" +
                        "</bookstore>")
                .build();

        // when
        final boolean isMatch = xmlBodyMatcher.match(stub, real);

        // then
        assertThat(isMatch).isTrue();
    }

    @Test
    void should_not_match_if_xml_not_valid() {
        // given
        final Request stub = Request.builder()
                .body("<bookstore> \n" +
                        "   <book nationality=\"ITALIAN\" category=\"COOKING\">\n" +
                        "       <title lang=\"en\">Everyday Italian</title>\n" +
                        "       <author>Giada De Laurentiis</author>\n" +
                        "       <year>2005</year>\n" +
                        "       <price>30.00</price>\n" +
                        "   </book>\n" +
                        ">")
                .build();
        final Request real = Request.builder()
                .body("<bookstore> \n" +
                        "   <book nationality=\"ITALIAN\" category=\"COOKING\">\n" +
                        "       <title lang=\"en\">Everyday Italian</title>\n" +
                        "       <author>Giada De Laurentiis</author>\n" +
                        "       <year>2005</year>\n" +
                        "       <price>30.00</price>\n" +
                        "   </book>\n" +
                        ">")
                .build();

        // when
        final boolean isMatch = xmlBodyMatcher.match(stub, real);

        // then
        assertThat(isMatch).isFalse();
    }

    @Test
    void should_applicable_if_xml() {
        // given
        final Request stub = Request.builder()
                .body("<bookstore> \n" +
                        "   <book nationality=\"ITALIAN\" category=\"COOKING\">\n" +
                        "       <title lang=\"en\">Everyday Italian</title>\n" +
                        "       <year>2005</year>\n" +
                        "       <author>Giada De Laurentiis</author>\n" +
                        "   </book>\n" +
                        "</bookstore>")
                .build();
        final Request real = Request.builder()
                .contentType("text/xml")
                .body("<bookstore> \n" +
                        "   <book nationality=\"ITALIAN\" category=\"COOKING\">\n" +
                        "       <title lang=\"en\">Everyday Italian</title>\n" +
                        "       <author>Giada De Laurentiis</author>\n" +
                        "       <year>2005</year>\n" +
                        "       <price>30.00</price>\n" +
                        "   </book>\n" +
                        "</bookstore>")
                .build();

        // when
        final boolean isApplicable = xmlBodyMatcher.isApplicable(stub, real);

        // then
        assertThat(isApplicable).isTrue();
    }

    @Test
    void should_not_applicable_if_xml_but_xpath() {
        // given
        final Request stub = Request.builder()
                .body("xpath:file")
                .build();
        final Request real = Request.builder()
                .contentType("text/xml")
                .body("xpath:/file")
                .build();

        // when
        final boolean isApplicable = xmlBodyMatcher.isApplicable(stub, real);

        // then
        assertThat(isApplicable).isFalse();
    }

    @Test
    void should_not_applicable_if_content_type_not_xml() {
        // given
        final Request real = Request.builder()
                .contentType("application/json")
                .build();

        // when
        final boolean isApplicable = xmlBodyMatcher.isApplicable(null, real);

        // then
        assertThat(isApplicable).isFalse();
    }

    @Test
    void should_not_match_if_body_not_xml() {
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
                .body("{\"name\":\"Juergen\",\"name\":\"Juergen\"}")
                .build();

        // when
        final boolean isMatch = xmlBodyMatcher.match(stub, real);

        // then
        assertThat(isMatch).isFalse();
    }
}

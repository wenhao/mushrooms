package com.github.wenhao.stub.matcher;

import com.github.wenhao.common.domain.Request;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class XpathBodyMatcherTest {


    private XpathBodyMatcher matcher;

    @BeforeEach
    void setUp() {
        matcher = new XpathBodyMatcher();
    }

    @Test
    void should_match_by_xpath() {
        // given
        final Request stubRequst = Request.builder()
                .body("xpath:/bookstore/book[price>30]/price")
                .build();
        final Request realRequest = Request.builder()
                .body("<bookstore>\n" +
                        "  <book category=\"COOKING\">\n" +
                        "    <title lang=\"en\">Everyday Italian</title>\n" +
                        "    <author>Giada De Laurentiis</author>\n" +
                        "    <year>2005</year>\n" +
                        "    <price>30.00</price>\n" +
                        "  </book>\n" +
                        "  <book category=\"CHILDREN\">\n" +
                        "    <title lang=\"en\">Harry Potter</title>\n" +
                        "    <author>J K. Rowling</author>\n" +
                        "    <year>2005</year>\n" +
                        "    <price>29.99</price>\n" +
                        "  </book>\n" +
                        "  <book category=\"WEB\">\n" +
                        "    <title lang=\"en\">Learning XML</title>\n" +
                        "    <author>Erik T. Ray</author>\n" +
                        "    <year>2003</year>\n" +
                        "    <price>31.95</price>\n" +
                        "  </book>\n" +
                        "</bookstore>")
                .build();


        // when
        final boolean isMatched = matcher.match(stubRequst, realRequest);

        // then
        assertThat(isMatched).isTrue();
    }

    @Test
    void should_not_match_if_xml_not_valid() {
        // given
        final Request stubRequst = Request.builder()
                .body("xpath:/bookstore/book[price>30]/price")
                .build();
        final Request realRequest = Request.builder()
                .body("<bookstore>\n" +
                        "  <book category=\"COOKING\">\n" +
                        "    <title lang=\"en\">Everyday Italian</title>\n" +
                        "    <author>Giada De Laurentiis</author>\n" +
                        "    <year>2005</year>\n" +
                        "    <price>30.00</price>\n" +
                        "  </book>\n" +
                        "  <book category=\"CHILDREN\">\n" +
                        "    <title lang=\"en\">Harry Potter</title>\n" +
                        "    <author>J K. Rowling</author>\n" +
                        "    <year>2005</year>\n" +
                        "    <price>29.99</price>\n" +
                        "  </book>\n" +
                        "  <book category=\"WEB\">\n" +
                        "    <title lang=\"en\">Learning XML</title>\n" +
                        "    <author>Erik T. Ray</author>\n" +
                        "   ")
                .build();


        // when
        final boolean isMatched = matcher.match(stubRequst, realRequest);

        // then
        assertThat(isMatched).isFalse();

    }

    @Test
    void should_applicable() {
        // given
        final Request stubRequst = Request.builder()
                .contentType("text/xml")
                .body("xpath:/bookstore/book[price>30]/price")
                .build();

        // when
        final boolean isApplicable = matcher.isApplicable(stubRequst);

        // then
        assertThat(isApplicable).isTrue();
    }

    @Test
    void should_not_applicable_if_xml_but_not_xpath() {
        // given
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
        final boolean isApplicable = matcher.isApplicable(real);

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
        final boolean isApplicable = matcher.isApplicable(real);

        // then
        assertThat(isApplicable).isFalse();
    }
}
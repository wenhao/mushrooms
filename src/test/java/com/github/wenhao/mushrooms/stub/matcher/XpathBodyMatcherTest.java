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

import com.github.wenhao.mushrooms.stub.domain.Request;
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
    void should_match_if_request_contains_namespace() {
        // given
        final Request stubRequst = Request.builder()
                .body("xpath:/Envelope/Body/GetBookRequest[BookName='Java']")
                .build();
        final Request realRequest = Request.builder()
                .body("<soap:Envelope\n" +
                        "        xmlns:soap=\"http://www.w3.org/2003/05/soap-envelope/\"\n" +
                        "        soap:encodingStyle=\"http://www.w3.org/2003/05/soap-encoding\">\n" +
                        "    <soap:Body xmlns:m=\"http://www.example.org/stock\">\n" +
                        "        <m:GetBookRequest>\n" +
                        "            <m:BookName>Java</m:BookName>\n" +
                        "        </m:GetBookRequest>\n" +
                        "    </soap:Body>\n" +
                        "</soap:Envelope>")
                .build();


        // when
        final boolean isMatched = matcher.match(stubRequst, realRequest);

        // then
        assertThat(isMatched).isTrue();
    }

    @Test
    void should_not_match_if_xpath_not_exist() {
        // given
        final Request stubRequst = Request.builder()
                .body("xpath:/bookstore/book[price>35]/price")
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
        assertThat(isMatched).isFalse();
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
        final boolean isApplicable = matcher.isApplicable(stubRequst, real);

        // then
        assertThat(isApplicable).isTrue();
    }

    @Test
    void should_not_applicable_if_xml_but_not_xpath() {
        // given
        final Request stubRequst = Request.builder()
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
        final boolean isApplicable = matcher.isApplicable(stubRequst, real);

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
        final boolean isApplicable = matcher.isApplicable(null, real);

        // then
        assertThat(isApplicable).isFalse();
    }
}
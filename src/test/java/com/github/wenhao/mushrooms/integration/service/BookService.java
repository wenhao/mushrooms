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

package com.github.wenhao.mushrooms.integration.service;

import com.github.wenhao.mushrooms.integration.client.SoapClient;
import com.github.wenhao.mushrooms.integration.domain.Book;
import com.github.wenhao.mushrooms.integration.request.BookRequest;
import com.google.common.collect.ImmutableMap;
import feign.Response;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Map;

import static com.github.wenhao.mushrooms.integration.domain.Templates.GET_BOOK_PRICE;

@Slf4j
@Service
@RequiredArgsConstructor
public class BookService {

    private final TemplateService templateService;
    private final SoapClient soapClient;
    private final XmlDeserializeService xmlDeserializeService;

    public Book get(String name) {
        final Map<String, Object> values = ImmutableMap.of("model", new BookRequest(name));
        final String requestBody = templateService.get(GET_BOOK_PRICE.getKey(), values);
        final Response response = soapClient.getBook(requestBody);
        return xmlDeserializeService.get(getResponseBody(response), GET_BOOK_PRICE.getValue(), Book.class);
    }

    private String getResponseBody(Response response) {
        try {
            return IOUtils.toString(response.body().asInputStream(), Charset.forName("UTF-8"));
        } catch (IOException e) {
            log.error("Couldn't get soap response.", e);
            return "";
        }
    }
}

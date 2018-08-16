package com.github.wenhao.integration.service;

import com.github.wenhao.integration.client.SoapClient;
import com.github.wenhao.integration.domain.Book;
import com.github.wenhao.integration.request.BookRequest;
import com.google.common.collect.ImmutableMap;
import feign.Response;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Map;

import static com.github.wenhao.integration.domain.Templates.GET_BOOK_PRICE;

@Slf4j
@Service
@RequiredArgsConstructor
public class BookService {

    private final TemplateService templateService;
    private final SoapClient soapClient;
    private final SoapService soapService;

    public Book get(String name) {
        final Map<String, Object> values = ImmutableMap.of("model", new BookRequest(name));
        final String requestBody = templateService.get(GET_BOOK_PRICE.getKey(), values);
        final Response response = soapClient.getBook(requestBody);
        return soapService.get(getResponseBody(response), GET_BOOK_PRICE.getValue(), Book.class);
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

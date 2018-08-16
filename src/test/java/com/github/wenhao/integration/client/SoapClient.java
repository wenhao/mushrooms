package com.github.wenhao.integration.client;

import feign.Headers;
import feign.Response;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient
@Headers("Content-Type: text/xml;charset=UTF-8")
public interface SoapClient {

    @PostMapping("stub/get_book")
    Response getBook(@RequestBody String request);
}

package com.github.wenhao.mushrooms.integration.client;

import feign.Response;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(
        name = "SOAP",
        url = "http://localhost:8080"
)
public interface SoapClient {

    @PostMapping(value = "stub/get_book", consumes = "text/xml;charset=UTF-8")
    Response getBook(@RequestBody String request);
}

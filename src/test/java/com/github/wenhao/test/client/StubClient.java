package com.github.wenhao.test.client;

import com.github.wenhao.common.domain.Header;
import com.github.wenhao.common.domain.Request;
import feign.Headers;
import feign.Response;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(
        name = "STUB",
        url = "http://localhost:8080"
)
public interface StubClient {

    @GetMapping("test")
    Header get();

    @GetMapping("stub")
    Header stub();

    @PostMapping("stub")
    Header postStub(@RequestBody Request request);

    @PostMapping("stub_soap")
    @Headers("Content-Type: text/xml;charset=UTF-8")
    Response soap(@RequestBody String request);
}

package com.github.wenhao.test.controller;

import com.github.wenhao.test.client.StubClient;
import com.github.wenhao.common.domain.Header;
import com.github.wenhao.common.domain.Request;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

@RestController
public class TestController {

    @Autowired
    private RestTemplate restTemplate;
    @Autowired
    private StubClient stubClient;

    @PostMapping("/resttemplate")
    public ResponseEntity restTemplate(@RequestBody Request request) {
        return restTemplate.getForEntity("http://localhost:8080/test", String.class);
    }

    @PostMapping("/okhttp")
    public ResponseEntity okHttp(@RequestBody Request request) {
        final Header header = stubClient.get();
        return ResponseEntity.ok(header);
    }
}

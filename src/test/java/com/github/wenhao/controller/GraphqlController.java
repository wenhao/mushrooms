package com.github.wenhao.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

@RestController
public class GraphqlController {

    @Autowired
    private RestTemplate restTemplate;

    @PostMapping("/graphql")
    public ResponseEntity index() {
//        final ResponseEntity<String> response = restTemplate.getForEntity("https://api.github.com/mata", String.class);
        final ResponseEntity<String> response = restTemplate.getForEntity("https://api.github.com/not-found", String.class);
        return ResponseEntity.ok(response.getBody());
    }
}

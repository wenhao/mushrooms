package com.github.wenhao.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class GraphqlController {

    @PostMapping("/graphql")
    public ResponseEntity index() {
        return ResponseEntity.ok("getFromReal");
    }
}

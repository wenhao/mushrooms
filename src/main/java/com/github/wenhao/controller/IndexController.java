package com.github.wenhao.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class IndexController {

  @PostMapping("/gateway")
  public ResponseEntity index() {
    return ResponseEntity.ok().build();
  }
}

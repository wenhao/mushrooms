package com.github.wenhao.test.controller;

import com.github.wenhao.common.domain.Header;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toList;
import static org.springframework.http.MediaType.APPLICATION_JSON_UTF8_VALUE;

@RestController
@RequestMapping("/test")
public class StubController {

    @GetMapping(consumes = APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity get() {
        String now = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
//        return ResponseEntity.ok(Header.builder().name("get").values(Stream.of(now).collect(toList())).build());
        return ResponseEntity.status(500).body(Header.builder().name("get").values(Stream.of(now).collect(toList())).build());
    }

    @PostMapping(consumes = APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity post() {
        String now = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        return ResponseEntity.ok(Header.builder().name("post").values(Stream.of(now).collect(toList())).build());
    }

}

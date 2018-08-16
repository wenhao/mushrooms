package com.github.wenhao.integration.controller;

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
import static org.springframework.http.MediaType.TEXT_XML_VALUE;

@RestController
@RequestMapping("/stub")
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

    @PostMapping(consumes = TEXT_XML_VALUE, value = "stub_soap")
    public ResponseEntity stubSoap() {
        String now = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        return ResponseEntity.status(500).build();
    }

    @PostMapping(consumes = TEXT_XML_VALUE, value = "book")
    public ResponseEntity getBook() {
        return ResponseEntity.status(500).build();
    }

}

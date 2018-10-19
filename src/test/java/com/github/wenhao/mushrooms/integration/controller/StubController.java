package com.github.wenhao.mushrooms.integration.controller;

import com.github.wenhao.mushrooms.integration.domain.Book;
import com.github.wenhao.mushrooms.integration.request.CreateBookRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;

import static org.springframework.http.MediaType.APPLICATION_JSON_UTF8_VALUE;
import static org.springframework.http.MediaType.TEXT_XML_VALUE;

@RestController
@RequestMapping("/stub")
public class StubController {

    @PostMapping(consumes = APPLICATION_JSON_UTF8_VALUE, value = "book")
    public ResponseEntity newBook(@RequestBody CreateBookRequest request) {
        final Book book = Book.builder()
                .name("Java")
                .price(new BigDecimal("34.5"))
                .build();
        return ResponseEntity.status(500).build();
//        return ResponseEntity.ok(book);
    }

    @PostMapping(consumes = TEXT_XML_VALUE, value = "get_book")
    public ResponseEntity getBook() {
        return ResponseEntity.status(500).build();
    }

}

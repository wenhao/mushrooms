package com.github.wenhao.mushrooms.integration.controller;

import com.github.wenhao.mushrooms.integration.client.RestClient;
import com.github.wenhao.mushrooms.integration.domain.Book;
import com.github.wenhao.mushrooms.integration.request.CreateBookRequest;
import com.github.wenhao.mushrooms.integration.service.BookService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

@RestController
@RequiredArgsConstructor
public class TestController {

    private final RestTemplate restTemplate;
    private final RestClient restClient;
    private final BookService bookService;

    @PostMapping("/book/resttemplate")
    public ResponseEntity<Book> restTemplate(@RequestBody CreateBookRequest request) {
        return restTemplate.postForEntity("http://localhost:8080/stub/book", request, Book.class);
    }

    @PostMapping("book/okhttp")
    public ResponseEntity<Book> newBook(@RequestBody CreateBookRequest request) {
        final Book book = restClient.newBook(request);
        return ResponseEntity.ok(book);
    }

    @GetMapping("book")
    public ResponseEntity<Book> getBook(@RequestParam String name) {
        final Book book = bookService.get(name);
        return ResponseEntity.ok(book);
    }
}

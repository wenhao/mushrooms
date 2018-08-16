package com.github.wenhao.integration.controller;

import com.github.wenhao.common.domain.Header;
import com.github.wenhao.common.domain.Request;
import com.github.wenhao.integration.client.StubClient;
import com.github.wenhao.integration.domain.Book;
import com.github.wenhao.integration.service.BookService;
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
    private final StubClient stubClient;
    private final BookService bookService;

    @PostMapping("/resttemplate")
    public ResponseEntity restTemplate(@RequestBody Request request) {
        return restTemplate.getForEntity("http://localhost:8080/test", String.class);
    }

    @PostMapping("/okhttp")
    public ResponseEntity okHttp(@RequestBody Request request) {
        final Header header = stubClient.get();
        return ResponseEntity.ok(header);
    }

    @PostMapping("/okhttp/stub")
    public ResponseEntity stub(@RequestBody Request request) {
        final Header header = stubClient.postStub(request);
        return ResponseEntity.ok(header);
    }

    @GetMapping("book")
    public ResponseEntity<Book> getBook(@RequestParam String name) {
        final Book book = bookService.get(name);
        return ResponseEntity.ok(book);
    }
}

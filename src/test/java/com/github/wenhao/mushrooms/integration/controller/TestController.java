/*
 * Copyright © 2019, Wen Hao <wenhao@126.com>.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

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

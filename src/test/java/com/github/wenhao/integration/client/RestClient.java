package com.github.wenhao.integration.client;

import com.github.wenhao.integration.domain.Book;
import com.github.wenhao.integration.request.CreateBookRequest;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(
        name = "REST",
        url = "http://localhost:8080"
)
public interface RestClient {

    @PostMapping("stub/book")
    Book newBook(@RequestBody CreateBookRequest request);
}

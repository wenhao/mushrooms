package com.github.wenhao.integration.request;

import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
public class BookRequest {

    private final String name;
}

package com.github.wenhao.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.net.URI;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CacheRequest implements Serializable {
    private URI uri;
    private String method;
    private String body;
    private List<Header> headers;
}

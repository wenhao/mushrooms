package com.github.wenhao.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class HttpRequest implements Serializable {
    private String path;
    private String method;
    private String body;
    private List<Header> headers;
    private List<Parameter> parameters;
    private List<Cookie> cookies;
}

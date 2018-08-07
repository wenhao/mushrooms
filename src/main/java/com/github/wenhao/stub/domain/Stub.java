package com.github.wenhao.stub.domain;

import lombok.Data;

@Data
public class Stub {

    private String uri;
    private String method;
    private String body;
    private String response;
}

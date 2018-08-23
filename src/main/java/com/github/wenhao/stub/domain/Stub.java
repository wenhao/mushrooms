package com.github.wenhao.stub.domain;

import com.github.wenhao.common.domain.Request;
import lombok.Data;

@Data
public class Stub {

    private Request request;
    private String response;
}

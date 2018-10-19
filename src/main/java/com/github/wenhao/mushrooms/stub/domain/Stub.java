package com.github.wenhao.mushrooms.stub.domain;

import com.github.wenhao.mushrooms.common.domain.Request;
import lombok.Data;

@Data
public class Stub {

    private Request request;
    private String response;
}

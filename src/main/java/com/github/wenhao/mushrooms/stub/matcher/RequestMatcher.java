package com.github.wenhao.mushrooms.stub.matcher;

import com.github.wenhao.mushrooms.stub.domain.Request;

@FunctionalInterface
public interface RequestMatcher {

    boolean match(Request stubRequest, Request realRequest);
}

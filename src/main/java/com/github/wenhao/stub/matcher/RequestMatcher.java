package com.github.wenhao.stub.matcher;

import com.github.wenhao.common.domain.Request;

@FunctionalInterface
public interface RequestMatcher {

    boolean match(Request stubRequest, Request realRequest);
}

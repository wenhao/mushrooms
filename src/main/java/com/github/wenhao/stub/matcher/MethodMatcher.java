package com.github.wenhao.stub.matcher;

import com.github.wenhao.common.domain.Request;

public class MethodMatcher implements RequestMatcher {

    @Override
    public boolean match(final Request stubRequest, final Request realRequest) {
        return realRequest.getMethod().matches(stubRequest.getMethod());
    }
}

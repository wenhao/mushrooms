package com.github.wenhao.mushrooms.stub.matcher;

import com.github.wenhao.mushrooms.common.domain.Request;

public class MethodMatcher implements RequestMatcher {

    @Override
    public boolean match(final Request stubRequest, final Request realRequest) {
        return realRequest.getMethod().matches(stubRequest.getMethod());
    }
}

package com.github.wenhao.mushrooms.stub.matcher;

import com.github.wenhao.mushrooms.stub.domain.Request;

public class PathMatcher implements RequestMatcher {

    @Override
    public boolean match(final Request stubRequest, final Request realRequest) {
        final String stubRequestPath = stubRequest.getPath();
        final String realRequestPath = realRequest.getPath();
        return realRequestPath.matches(stubRequestPath);
    }
}

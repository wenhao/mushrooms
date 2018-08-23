package com.github.wenhao.stub.matcher;

import com.github.wenhao.common.domain.Request;

public class PathMatcher implements RequestMatcher {

    @Override
    public boolean match(final Request stubRequest, final Request realRequest) {
        final String stubRequestPath = stubRequest.getPath();
        final String realRequestPath = realRequest.getPath();
        return realRequestPath.matches(stubRequestPath);
    }
}

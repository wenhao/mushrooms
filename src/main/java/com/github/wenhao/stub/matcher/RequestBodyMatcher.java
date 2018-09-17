package com.github.wenhao.stub.matcher;

import com.github.wenhao.common.domain.Request;

public interface RequestBodyMatcher {

    boolean isApplicable(Request stubRequest, Request realRequest);

    boolean match(Request stubRequest, Request realRequest);
}

package com.github.wenhao.mushrooms.stub.matcher;

import com.github.wenhao.mushrooms.stub.domain.Request;

public interface RequestBodyMatcher {

    boolean isApplicable(Request stubRequest, Request realRequest);

    boolean match(Request stubRequest, Request realRequest);
}

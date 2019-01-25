package com.github.wenhao.mushrooms.stub.matcher;

import com.github.wenhao.mushrooms.stub.domain.Request;

import static org.springframework.util.CollectionUtils.isEmpty;


public class HeaderMatcher implements RequestMatcher {

    @Override
    public boolean match(final Request stubRequest, final Request realRequest) {
        if (isEmpty(stubRequest.getHeaders())) {
            return true;
        }
        return realRequest.getHeaders().stream()
                .allMatch(header -> stubRequest.getHeaders().stream()
                        .anyMatch(stubHeader -> header.getName().matches(stubHeader.getName()) &&
                                header.getValue().matches(stubHeader.getValue())));
    }
}

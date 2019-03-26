package com.github.wenhao.mushrooms.stub.matcher;

import com.github.wenhao.mushrooms.stub.domain.Request;

import java.util.Objects;


public class HeaderMatcher implements RequestMatcher {

    @Override
    public boolean match(final Request stubRequest, final Request realRequest) {
        if (Objects.isNull(stubRequest.getHeaders()) || stubRequest.getHeaders().isEmpty()) {
            return true;
        }
        return realRequest.getHeaders().stream()
                .allMatch(header -> stubRequest.getHeaders().stream()
                        .anyMatch(stubHeader -> header.getName().matches(stubHeader.getName()) &&
                                header.getValue().matches(stubHeader.getValue())));
    }
}

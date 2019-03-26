package com.github.wenhao.mushrooms.stub.matcher;

import com.github.wenhao.mushrooms.stub.domain.Request;

import java.util.Objects;


public class ParameterMatcher implements RequestMatcher {

    @Override
    public boolean match(final Request stubRequest, final Request realRequest) {
        if (Objects.isNull(stubRequest.getParameters()) || stubRequest.getParameters().isEmpty()) {
            return true;
        }
        return realRequest.getParameters().stream()
                .allMatch(parameter -> stubRequest.getParameters().stream()
                        .anyMatch(stubParameter -> parameter.getName().matches(stubParameter.getName()) &&
                                parameter.getValue().matches(stubParameter.getValue())));
    }
}

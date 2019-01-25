package com.github.wenhao.mushrooms.stub.matcher;

import com.github.wenhao.mushrooms.stub.domain.Request;

import static org.springframework.util.CollectionUtils.isEmpty;


public class ParameterMatcher implements RequestMatcher {

    @Override
    public boolean match(final Request stubRequest, final Request realRequest) {
        if (isEmpty(stubRequest.getParameters())) {
            return true;
        }
        return realRequest.getParameters().stream()
                .allMatch(parameter -> stubRequest.getParameters().stream()
                        .anyMatch(stubParameter -> parameter.getName().matches(stubParameter.getName()) &&
                                parameter.getValue().matches(stubParameter.getValue())));
    }
}

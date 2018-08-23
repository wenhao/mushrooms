package com.github.wenhao.stub.matcher;

import com.github.wenhao.common.domain.Request;

import static org.apache.commons.collections.CollectionUtils.isEmpty;

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

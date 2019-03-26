package com.github.wenhao.mushrooms.stub.matcher;

import com.github.wenhao.mushrooms.stub.domain.Request;

import java.util.List;

import lombok.AllArgsConstructor;
import static org.apache.commons.lang3.StringUtils.isBlank;

@AllArgsConstructor
public class BodyMatcher implements RequestMatcher {

    private final List<RequestBodyMatcher> requestBodyMatchers;

    @Override
    public boolean match(final Request stubRequest, final Request realRequest) {
        if (isBlank(stubRequest.getBody())) {
            return true;
        }
        return requestBodyMatchers.stream()
                .filter(matcher -> matcher.isApplicable(stubRequest, realRequest))
                .findFirst()
                .map(matcher -> matcher.match(stubRequest, realRequest))
                .orElse(false);
    }
}

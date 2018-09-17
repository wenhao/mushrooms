package com.github.wenhao.stub.matcher;

import com.github.wenhao.common.domain.Request;
import lombok.RequiredArgsConstructor;

import java.util.List;

import static org.apache.commons.lang.StringUtils.isBlank;

@RequiredArgsConstructor
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

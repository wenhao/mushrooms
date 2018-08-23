package com.github.wenhao.stub.matcher;

import com.github.wenhao.common.domain.Request;

import static org.apache.commons.collections.CollectionUtils.isEmpty;

public class CookieMatcher implements RequestMatcher {

    @Override
    public boolean match(final Request stubRequest, final Request realRequest) {
        if(isEmpty(stubRequest.getCookies())){
            return true;
        }
        return realRequest.getCookies().stream()
                .allMatch(cookie -> stubRequest.getCookies().stream()
                        .anyMatch(stubCookie -> cookie.getName().matches(stubCookie.getName()) &&
                                cookie.getValue().matches(stubCookie.getValue())));
    }
}

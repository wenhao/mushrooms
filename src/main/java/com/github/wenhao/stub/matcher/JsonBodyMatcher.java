package com.github.wenhao.stub.matcher;

import com.github.wenhao.common.domain.Request;
import org.json.JSONException;
import org.skyscreamer.jsonassert.JSONCompare;

import static org.skyscreamer.jsonassert.JSONCompareMode.LENIENT;

public class JsonBodyMatcher implements RequestBodyMatcher {

    @Override
    public boolean isApplicable(final Request request) {
        return request.getContentType().contains("json");
    }

    @Override
    public boolean match(final Request stubRequest, final Request realRequest) {
        try {
            return JSONCompare.compareJSON(stubRequest.getBody(), realRequest.getBody(), LENIENT).passed();
        } catch (JSONException e) {
            return false;
        }
    }
}

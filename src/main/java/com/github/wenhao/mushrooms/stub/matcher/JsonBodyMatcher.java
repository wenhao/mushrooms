package com.github.wenhao.mushrooms.stub.matcher;

import com.github.wenhao.mushrooms.stub.domain.Request;
import org.json.JSONException;
import org.skyscreamer.jsonassert.JSONCompare;

import static org.skyscreamer.jsonassert.JSONCompareMode.LENIENT;

public class JsonBodyMatcher implements RequestBodyMatcher {

    @Override
    public boolean isApplicable(final Request stubRequest, final Request realRequest) {
        return realRequest.getContentType().contains("json");
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

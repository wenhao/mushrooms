package com.github.wenhao.mushrooms.stub.matcher;

import com.github.wenhao.mushrooms.common.domain.Request;
import org.json.JSONException;
import org.json.XML;
import org.skyscreamer.jsonassert.JSONCompare;

import static org.skyscreamer.jsonassert.JSONCompareMode.LENIENT;

public class XMLBodyMatcher implements RequestBodyMatcher {

    @Override
    public boolean isApplicable(final Request stubRequest, final Request realRequest) {
        return realRequest.getContentType().contains("xml") && !stubRequest.getBody().startsWith("xpath:");
    }

    @Override
    public boolean match(final Request stubRequest, final Request realRequest) {
        try {
            String stubBodyJson = XML.toJSONObject(stubRequest.getBody()).toString();
            String realBodyJson = XML.toJSONObject(realRequest.getBody()).toString();
            return JSONCompare.compareJSON(stubBodyJson, realBodyJson, LENIENT).passed();
        } catch (JSONException e) {
            return false;
        }
    }
}

package com.github.wenhao.stub.matcher;

import lombok.RequiredArgsConstructor;
import org.json.XML;

@RequiredArgsConstructor
public class BodyMatcher {

    private final JsonMatcher jsonMatcher;

    public boolean isMatch(String stubBody, String realBody) {
        if (isSoapRequest(realBody)) {
            String stubBodyJson = XML.toJSONObject(stubBody).toString();
            String realBodyJson = XML.toJSONObject(realBody).toString();
            return jsonMatcher.isJsonMatch(stubBodyJson, realBodyJson);
        }
        return jsonMatcher.isJsonMatch(stubBody, realBody);
    }

    public boolean isSoapRequest(final String realBody) {
        return realBody.contains("<soap");
    }

}

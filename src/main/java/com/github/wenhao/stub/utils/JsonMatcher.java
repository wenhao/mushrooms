package com.github.wenhao.stub.utils;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.fge.jsonpatch.diff.JsonDiff;
import lombok.RequiredArgsConstructor;

import java.io.IOException;

@RequiredArgsConstructor
public class JsonMatcher {

    private final ObjectMapper mapper;

    public boolean isJsonMatch(String source, String target) {
        try {
            JsonNode jsonNode = mapper.readTree(source);
            final JsonNode stubJson = mapper.readTree(target);
            return JsonDiff.asJson(jsonNode, stubJson).findValuesAsText("op").stream()
                    .allMatch(op -> op.equals("remove"));
        } catch (IOException e) {
            return false;
        }
    }
}

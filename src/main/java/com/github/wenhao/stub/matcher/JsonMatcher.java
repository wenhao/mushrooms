package com.github.wenhao.stub.matcher;

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
            JsonNode stubJson = mapper.readTree(source);
            JsonNode jsonNode = mapper.readTree(target);
            return JsonDiff.asJson(stubJson, jsonNode).findValuesAsText("op").stream()
                    .allMatch(op -> op.equals("add"));
        } catch (IOException e) {
            return false;
        }
    }
}

package com.github.wenhao.stub.dataloader;

import com.github.wenhao.common.domain.Response;
import com.github.wenhao.stub.domain.Stub;
import com.github.wenhao.stub.matcher.BodyMatcher;
import lombok.RequiredArgsConstructor;

import java.util.Optional;

import static com.github.wenhao.common.domain.Response.TEXT_XML_UTF8;

@RequiredArgsConstructor
public class StubResponseDataLoader {

    private final ResourceReader resourceReader;
    private final BodyMatcher bodyMatcher;

    public Response load(Stub stub) {
        final String body = resourceReader.readAsString(stub.getResponse());
        return Optional.ofNullable(body)
                .filter(bodyMatcher::isSoapRequest)
                .map(it -> Response.builder()
                        .contentType(TEXT_XML_UTF8)
                        .body(body)
                        .build())
                .orElse(Response.builder()
                        .body(body)
                        .build());
    }
}

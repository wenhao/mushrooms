package com.github.wenhao.stub.dataloader;

import com.github.wenhao.common.domain.Request;
import com.github.wenhao.stub.domain.Stub;

import java.net.URI;
import java.net.URISyntaxException;

public interface DataLoader {

    boolean isApplicable(Stub stub);

    void load(Stub stub);

    default Request getRequest(final Stub stub) {
        URI uri = getUri(stub);
        return Request.builder()
                .uri(uri)
                .method(stub.getMethod())
                .build();
    }

    default URI getUri(final Stub stub) {
        URI uri = null;
        try {
            uri = new URI(stub.getUri());
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        return uri;
    }
}

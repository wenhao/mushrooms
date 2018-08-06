package com.github.wenhao.stub.dataloader;

import com.github.wenhao.common.domain.Request;
import com.github.wenhao.common.domain.Response;
import com.github.wenhao.stub.domain.Stub;
import com.github.wenhao.stub.repository.StubRepository;
import com.google.common.io.Resources;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;

import java.io.IOException;
import java.nio.charset.Charset;

@Slf4j
@RequiredArgsConstructor
public class JsonDataLoader implements DataLoader {

    private final ResourceLoader resourceLoader;
    private final StubRepository repository;

    @Override
    public boolean isApplicable(Stub stub) {
        return stub.getResponse().endsWith(".json");
    }

    @Override
    public void load(Stub stub) {
        final Request stubRequest = getRequest(stub);
        final String response = stub.getResponse();
        final Resource resource = resourceLoader.getResource("classpath:" + response);
        repository.save(stubRequest, Response.builder()
                .body(getBody(resource))
                .build());
    }

    private String getBody(final Resource resource) {
        try {
            return Resources.toString(resource.getURL(), Charset.forName("UTF8"));
        } catch (IOException e) {
            log.error("[MUSHROOMS]Read stub json response file failure.", e);
            return "";
        }
    }
}

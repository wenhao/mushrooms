package com.github.wenhao.stub.dataloader;

import com.github.wenhao.stub.domain.Stub;
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

    @Override
    public boolean isApplicable(Stub stub) {
        return stub.getResponse().endsWith(".json");
    }

    @Override
    public String load(Stub stub) {
        final Resource resource = resourceLoader.getResource("classpath:" + stub.getResponse());
        return getBody(resource);
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

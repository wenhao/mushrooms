package com.github.wenhao.mushrooms.stub.dataloader;

import com.google.common.io.Resources;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;

import java.io.IOException;
import java.nio.charset.Charset;

@Slf4j
@RequiredArgsConstructor
public class ResourceReader {

    private final ResourceLoader resourceLoader;

    public String readAsString(String path) {
        final Resource resource = resourceLoader.getResource("classpath:" + path);
        return getBody(resource);
    }

    private String getBody(final Resource resource) {
        try {
            return Resources.toString(resource.getURL(), Charset.forName("UTF8"));
        } catch (IOException e) {
            log.error("[MUSHROOMS]Failure to read stub json response file.", e);
            return "";
        }
    }
}

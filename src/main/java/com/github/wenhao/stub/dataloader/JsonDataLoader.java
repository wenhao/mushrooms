package com.github.wenhao.stub.dataloader;

import com.github.wenhao.stub.domain.Stub;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class JsonDataLoader implements DataLoader {

    private final ResourceReader resourceReader;

    @Override
    public boolean isApplicable(Stub stub) {
        return stub.getResponse().endsWith(".json");
    }

    @Override
    public String load(Stub stub) {
        return resourceReader.readAsString(stub.getResponse());
    }
}

package com.github.wenhao.stub.dataloader;

import com.github.wenhao.stub.properties.MushroomsStubConfigurationProperties;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;

import javax.annotation.PostConstruct;
import java.util.List;

@RequiredArgsConstructor
public class StubDataLoader {

    private final MushroomsStubConfigurationProperties properties;
    private final List<DataLoader> dataLoaders;

    @PostConstruct
    public void load() {
        properties.getOkhttp().getStubs()
                .forEach(stub -> Flux.fromIterable(dataLoaders)
                        .filter(dataLoader -> dataLoader.isApplicable(stub))
                        .next()
                        .block()
                        .load(stub));
    }
}

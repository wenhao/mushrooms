package com.github.wenhao.stub.dataloader;

import com.github.wenhao.stub.domain.Stub;

public interface DataLoader {

    boolean isApplicable(Stub stub);

    String load(Stub stub);
}

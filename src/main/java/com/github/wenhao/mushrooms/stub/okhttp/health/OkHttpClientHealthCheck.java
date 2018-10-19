package com.github.wenhao.mushrooms.stub.okhttp.health;

import okhttp3.Response;

@FunctionalInterface
public interface OkHttpClientHealthCheck {

    boolean health(Response response);
}

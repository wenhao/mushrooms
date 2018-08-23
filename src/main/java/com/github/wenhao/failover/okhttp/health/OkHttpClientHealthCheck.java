package com.github.wenhao.failover.okhttp.health;

import okhttp3.Response;

@FunctionalInterface
public interface OkHttpClientHealthCheck {

    boolean health(Response response);
}

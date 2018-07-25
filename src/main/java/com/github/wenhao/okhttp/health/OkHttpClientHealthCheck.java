package com.github.wenhao.okhttp.health;

import okhttp3.Response;

public interface OkHttpClientHealthCheck {
    boolean health(Response response);
}

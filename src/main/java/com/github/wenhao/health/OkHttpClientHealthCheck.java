package com.github.wenhao.health;

import okhttp3.Response;

public interface OkHttpClientHealthCheck {
    boolean health(Response response);
}

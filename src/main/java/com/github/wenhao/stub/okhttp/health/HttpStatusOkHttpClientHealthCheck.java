package com.github.wenhao.stub.okhttp.health;

import okhttp3.Response;

public class HttpStatusOkHttpClientHealthCheck implements OkHttpClientHealthCheck {

    @Override
    public boolean health(final Response response) {
        return response.code() == 200;
    }
}

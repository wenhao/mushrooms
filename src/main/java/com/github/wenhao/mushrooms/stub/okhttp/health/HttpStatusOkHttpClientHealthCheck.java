package com.github.wenhao.mushrooms.stub.okhttp.health;

import okhttp3.Response;

public class HttpStatusOkHttpClientHealthCheck implements OkHttpClientHealthCheck {

    @Override
    public boolean health(final Response response) {
        return response.code() == 200;
    }
}

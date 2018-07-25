package com.github.wenhao.health;

import okhttp3.Response;
import org.springframework.stereotype.Component;

@Component
public class HttpStatusOkHttpClientHealthCheck implements OkHttpClientHealthCheck {
    @Override
    public boolean health(final Response response) {
        return response.code() == 200;
    }
}

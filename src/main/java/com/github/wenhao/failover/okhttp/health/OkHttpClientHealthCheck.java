package com.github.wenhao.failover.okhttp.health;

import okhttp3.MediaType;
import okhttp3.Response;
import okio.Buffer;
import okio.BufferedSource;

import java.nio.charset.Charset;

@FunctionalInterface
public interface OkHttpClientHealthCheck {

    Charset UTF8 = Charset.forName("UTF-8");

    boolean health(Response response);

    default String getResponseBody(final Response response) {
        try {
            BufferedSource source = response.body().source();
            source.request(Long.MAX_VALUE);
            Buffer buffer = source.buffer();
            Charset charset = UTF8;
            MediaType contentType = response.body().contentType();
            if (contentType != null) {
                charset = contentType.charset(UTF8);
            }
            return buffer.clone().readString(charset);
        } catch (Exception e) {
            return "";
        }
    }
}

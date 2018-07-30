package com.github.wenhao.failover.okhttp.interceptor;

import com.github.wenhao.common.domain.Header;
import com.github.wenhao.common.domain.Request;
import com.github.wenhao.failover.okhttp.health.OkHttpClientHealthCheck;
import com.github.wenhao.failover.properties.MushroomsFailoverConfigurationProperties;
import com.github.wenhao.failover.repository.FailoverRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;
import okio.Buffer;
import okio.BufferedSource;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static java.util.stream.Collectors.toList;
import static org.springframework.http.HttpStatus.OK;
import static org.springframework.util.CollectionUtils.isEmpty;

@Slf4j
@RequiredArgsConstructor
public class CachingOkHttpClientInterceptor implements Interceptor {

    private static final Charset UTF8 = Charset.forName("UTF-8");
    private final FailoverRepository repository;
    private final MushroomsFailoverConfigurationProperties properties;
    private final List<OkHttpClientHealthCheck> healthChecks;

    @Override
    public Response intercept(final Chain chain) throws IOException {
        okhttp3.Request request = chain.request();

        final Request cacheRequest = Request.builder()
                .uri(request.url().uri())
                .headers(getHeaders(request))
                .method(request.method())
                .body(Optional.ofNullable(request.body()).map(this::getRequestBody).orElse(""))
                .build();

        final Response response = chain.proceed(request);
        boolean isHealth = healthChecks.stream().allMatch(okHttpClientInterceptor -> okHttpClientInterceptor.health(response));
        if (isHealth) {
            log.debug("[MUSHROOMS]Refresh cached data for request\n{}.", cacheRequest.toString());
            repository.save(cacheRequest, getResponseBody(response.body()));
            return new Response.Builder()
                    .code(OK.value())
                    .request(request)
                    .headers(response.headers())
                    .body(ResponseBody.create(response.body().contentType(), getResponseBody(response.body())))
                    .cacheResponse(response.cacheResponse())
                    .handshake(response.handshake())
                    .message(response.message())
                    .networkResponse(response.networkResponse())
                    .priorResponse(response.priorResponse())
                    .protocol(response.protocol())
                    .receivedResponseAtMillis(response.receivedResponseAtMillis())
                    .build();
        }
        return Optional.ofNullable(repository.get(cacheRequest))
                .map(cache -> {
                    log.debug("[MUSHROOMS]Respond with cached data for request\n{}.", cacheRequest.toString());
                    return cache;
                })
                .map(bodyString -> new Response.Builder()
                        .code(OK.value())
                        .request(request)
                        .headers(response.headers())
                        .body(ResponseBody.create(response.body().contentType(), bodyString))
                        .cacheResponse(response.cacheResponse())
                        .handshake(response.handshake())
                        .message(response.message())
                        .networkResponse(response.networkResponse())
                        .priorResponse(response.priorResponse())
                        .protocol(response.protocol())
                        .receivedResponseAtMillis(response.receivedResponseAtMillis())
                        .build())
                .orElse(new Response.Builder()
                        .code(OK.value())
                        .request(request)
                        .headers(response.headers())
                        .body(ResponseBody.create(response.body().contentType(), ""))
                        .cacheResponse(response.cacheResponse())
                        .handshake(response.handshake())
                        .message(response.message())
                        .networkResponse(response.networkResponse())
                        .priorResponse(response.priorResponse())
                        .protocol(response.protocol())
                        .receivedResponseAtMillis(response.receivedResponseAtMillis())
                        .build());
    }

    private List<Header> getHeaders(final okhttp3.Request request) {
        final Map<String, List<String>> headerMap = request.headers().toMultimap();
        final List<Header> headers = headerMap.keySet().stream()
                .map(name -> Header.builder().name(name).values(headerMap.get(name)).build())
                .collect(toList());
        if (!isEmpty(properties.getHeaders())) {
            return headers.stream()
                    .filter(header -> properties.getHeaders().contains(header.getName()))
                    .collect(toList());
        }
        return headers;
    }

    private String getRequestBody(final RequestBody requestBody) {
        Buffer buffer = new Buffer();
        try {
            requestBody.writeTo(buffer);
            Charset charset = UTF8;
            MediaType contentType = requestBody.contentType();
            if (contentType != null) {
                charset = contentType.charset(UTF8);
            }
            return buffer.readString(charset);
        } catch (IOException e) {
            return "";
        }
    }

    private String getResponseBody(final ResponseBody responseBody) {
        try {
            BufferedSource source = responseBody.source();
            source.request(Long.MAX_VALUE);
            Buffer buffer = source.buffer();
            Charset charset = UTF8;
            MediaType contentType = responseBody.contentType();
            if (contentType != null) {
                charset = contentType.charset(UTF8);
            }
            return buffer.clone().readString(charset);
        } catch (IOException e) {
            return "";
        }
    }
}

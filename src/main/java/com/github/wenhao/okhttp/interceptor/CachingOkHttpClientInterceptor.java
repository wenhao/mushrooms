package com.github.wenhao.okhttp.interceptor;

import com.github.wenhao.common.config.CachingConfigurationProperties;
import com.github.wenhao.common.domain.Header;
import com.github.wenhao.common.domain.Request;
import com.github.wenhao.common.repository.CachingRepository;
import com.github.wenhao.okhttp.health.OkHttpClientHealthCheck;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;
import okio.Buffer;
import okio.BufferedSource;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static java.util.stream.Collectors.toList;
import static org.springframework.http.HttpStatus.OK;
import static org.springframework.util.CollectionUtils.isEmpty;

@Slf4j
@Component
@ConditionalOnProperty(value = "parrot.okhttp.enabled", havingValue = "true")
@RequiredArgsConstructor
public class CachingOkHttpClientInterceptor implements Interceptor {

    private static final Charset UTF8 = Charset.forName("UTF-8");
    private final CachingRepository cachingRepository;
    private final CachingConfigurationProperties cachingConfigurationProperties;
    private final List<OkHttpClientHealthCheck> okHttpClientHealthChecks;

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
        boolean isHealth = okHttpClientHealthChecks.stream().allMatch(okHttpClientInterceptor -> okHttpClientInterceptor.health(response));
        if (isHealth) {
            log.info("[PARROT]Refresh cached data for {}.", cacheRequest.toString());
            cachingRepository.save(cacheRequest, getResponseBody(response.body()));
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

        log.info("[PARROT]Respond with cached data for {}.", cacheRequest.toString());
        return Optional.ofNullable(cachingRepository.get(cacheRequest))
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
        if (!isEmpty(cachingConfigurationProperties.getHeaders())) {
            return headers.stream()
                    .filter(header -> cachingConfigurationProperties.getHeaders().contains(header.getName()))
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

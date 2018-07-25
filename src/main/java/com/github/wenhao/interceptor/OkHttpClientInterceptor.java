package com.github.wenhao.interceptor;

import com.github.wenhao.config.ParrotConfiguration;
import com.github.wenhao.domain.CacheRequest;
import com.github.wenhao.domain.Header;
import com.github.wenhao.health.OkHttpClientHealthCheck;
import com.github.wenhao.repository.ParrotCacheRepository;
import lombok.RequiredArgsConstructor;
import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;
import okio.Buffer;
import okio.BufferedSource;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static io.lettuce.core.protocol.LettuceCharsets.UTF8;
import static java.util.stream.Collectors.toList;
import static org.springframework.http.HttpStatus.OK;
import static org.springframework.util.CollectionUtils.isEmpty;

@Component
@RequiredArgsConstructor
public class OkHttpClientInterceptor implements Interceptor {

    private final ParrotCacheRepository parrotCacheRepository;
    private final ParrotConfiguration parrotConfiguration;
    private final List<OkHttpClientHealthCheck> okHttpClientHealthChecks;

    @Override
    public Response intercept(final Chain chain) throws IOException {
        Request request = chain.request();

        final CacheRequest cacheRequest = CacheRequest.builder()
                .uri(request.url().uri())
                .headers(getHeaders(request))
                .method(request.method())
                .body(Optional.ofNullable(request.body()).map(this::getRequestBody).orElse(""))
                .build();

        final Response response = chain.proceed(request);
        boolean isHealth = okHttpClientHealthChecks.stream().allMatch(okHttpClientInterceptor -> okHttpClientInterceptor.health(response));
        if (isHealth) {
            parrotCacheRepository.save(cacheRequest, getResponseBody(response.body()));
            return response;
        }

        return Optional.ofNullable(parrotCacheRepository.get(cacheRequest))
                .map(bodyString -> new Response.Builder()
                        .code(OK.value())
                        .headers(response.headers())
                        .body(ResponseBody.create(response.body().contentType(), bodyString))
                        .build())
                .orElse(new Response.Builder()
                        .code(OK.value())
                        .headers(response.headers())
                        .body(ResponseBody.create(response.body().contentType(), ""))
                        .build());
    }

    private List<Header> getHeaders(final Request request) {
        final Map<String, List<String>> headerMap = request.headers().toMultimap();
        final List<Header> headers = headerMap.keySet().stream()
                .map(name -> Header.builder().name(name).values(headerMap.get(name)).build())
                .collect(toList());
        if (!isEmpty(parrotConfiguration.getHeaders())) {
            return headers.stream()
                    .filter(header -> parrotConfiguration.getHeaders().contains(header.getName()))
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
        BufferedSource source = responseBody.source();
        Buffer buffer = source.buffer();
        Charset charset = UTF8;
        MediaType contentType = responseBody.contentType();
        if (contentType != null) {
            charset = contentType.charset(UTF8);
        }
        return buffer.readString(charset);
    }
}

package com.github.wenhao.failover.okhttp.interceptor;

import com.github.wenhao.common.domain.Header;
import com.github.wenhao.common.domain.Request;
import com.github.wenhao.common.domain.Response;
import com.github.wenhao.failover.okhttp.health.OkHttpClientHealthCheck;
import com.github.wenhao.failover.properties.MushroomsFailoverConfigurationProperties;
import com.github.wenhao.failover.repository.FailoverRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import okhttp3.Headers;
import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import okio.Buffer;
import okio.BufferedSource;
import reactor.core.publisher.Mono;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.List;
import java.util.Optional;

import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toMap;
import static okhttp3.Protocol.HTTP_1_1;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;
import static org.springframework.http.HttpStatus.OK;

@Slf4j
@RequiredArgsConstructor
public class CachingOkHttpClientInterceptor implements Interceptor {

    private static final Charset UTF8 = Charset.forName("UTF-8");
    private static final MediaType APPLICATION_JSON_UTF8 = MediaType.parse("application/json;charset=UTF-8");
    private final FailoverRepository repository;
    private final MushroomsFailoverConfigurationProperties properties;
    private final List<OkHttpClientHealthCheck> healthChecks;

    @Override
    public okhttp3.Response intercept(final Chain chain) throws IOException {
        okhttp3.Request request = chain.request();

        final Request cacheRequest = Request.builder()
                .uri(request.url().uri())
                .headers(getHeaders(request))
                .method(request.method())
                .body(Optional.ofNullable(request.body()).map(this::getRequestBody).orElse(""))
                .build();

        final okhttp3.Response response = getRemoteResponse(chain, request);
        final boolean isExcluded = properties.getExcludes().stream().anyMatch(exclude -> cacheRequest.getUrlButParameters().endsWith(exclude));
        if (isExcluded) {
            return response;
        }
        boolean isHealth = healthChecks.stream().allMatch(okHttpClientInterceptor -> okHttpClientInterceptor.health(response));
        if (isHealth) {
            log.debug("[MUSHROOMS]Refresh cached data for request\n{}.", cacheRequest.toString());
            final Response cacheResponse = Response.builder().headers(response.headers().toMultimap().entrySet().stream()
                    .map(entry -> Header.builder().name(entry.getKey()).values(entry.getValue()).build())
                    .collect(toList()))
                    .body(getResponseBody(response.body()))
                    .build();
            repository.save(cacheRequest, cacheResponse);
            return getResponse(request, response, cacheResponse);
        }
        return Optional.ofNullable(repository.get(cacheRequest))
                .map(resp -> {
                    log.debug("[MUSHROOMS]Respond with cached data for request\n{}.", cacheRequest.toString());
                    return resp;
                })
                .map(resp -> getResponse(request, response, resp))
                .orElse(getResponse(request, response, Response.empty()));
    }

    private okhttp3.Response getResponse(final okhttp3.Request request, final okhttp3.Response response, final Response cacheResponse) {
        return Mono.just(new okhttp3.Response.Builder())
                .map(resp -> resp.code(OK.value()))
                .map(resp -> resp.request(request))
                .map(resp -> resp.message(response.message()))
                .map(resp -> resp.protocol(response.protocol()))
                .map(resp -> Optional.ofNullable(response.body())
                        .map(respBody -> resp.body(ResponseBody.create(respBody.contentType(), cacheResponse.getBody())))
                        .orElse(resp.body(ResponseBody.create(APPLICATION_JSON_UTF8, cacheResponse.getBody()))))
                .map(resp -> resp.headers(Headers.of(cacheResponse.getHeaders().stream()
                        .collect(toMap(Header::getName, header -> header.getValues().get(0))))))
                .map(resp -> Optional.ofNullable(response.cacheResponse()).map(resp::cacheResponse).orElse(resp))
                .map(resp -> Optional.ofNullable(response.handshake()).map(resp::handshake).orElse(resp))
                .map(resp -> Optional.ofNullable(response.networkResponse()).map(resp::networkResponse).orElse(resp))
                .map(resp -> Optional.ofNullable(response.priorResponse()).map(resp::priorResponse).orElse(resp))
                .map(resp -> resp.receivedResponseAtMillis(response.receivedResponseAtMillis()))
                .block()
                .build();
    }

    private okhttp3.Response getRemoteResponse(final Chain chain, final okhttp3.Request request) {
        try {
            return chain.proceed(request);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return new okhttp3.Response.Builder()
                    .code(INTERNAL_SERVER_ERROR.value())
                    .request(request)
                    .message(e.getMessage())
                    .protocol(HTTP_1_1)
                    .body(ResponseBody.create(APPLICATION_JSON_UTF8, ""))
                    .headers(Headers.of("Content-Type", APPLICATION_JSON_UTF8.toString()))
                    .build();
        }
    }

    private List<Header> getHeaders(final okhttp3.Request request) {
        final List<Header> headers = request.headers().toMultimap().entrySet().stream()
                .map(entry -> Header.builder().name(entry.getKey()).values(entry.getValue()).build())
                .collect(toList());
        return headers.stream()
                .filter(header -> properties.getHeaders().contains(header.getName()))
                .collect(toList());
    }

    private String getRequestBody(final RequestBody requestBody) {
        Buffer buffer = new Buffer();
        try {
            requestBody.writeTo(buffer);
            Charset charset = Optional.ofNullable(requestBody.contentType()).map(contentType -> contentType.charset(UTF8)).orElse(UTF8);
            return buffer.readString(charset);
        } catch (Exception e) {
            return "";
        }
    }

    private String getResponseBody(final ResponseBody responseBody) {
        try {
            BufferedSource source = responseBody.source();
            source.request(Long.MAX_VALUE);
            Buffer buffer = source.buffer();
            Charset charset = Optional.ofNullable(responseBody.contentType()).map(contentType -> contentType.charset(UTF8)).orElse(UTF8);
            return buffer.clone().readString(charset);
        } catch (Exception e) {
            return "";
        }
    }
}

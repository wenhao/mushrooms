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

import static java.util.Objects.nonNull;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toMap;
import static okhttp3.Protocol.HTTP_1_1;
import static org.apache.commons.lang.StringUtils.substringBefore;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;
import static org.springframework.http.HttpStatus.OK;

@Slf4j
@RequiredArgsConstructor
public class CachingOkHttpClientInterceptor implements Interceptor {

    private static final Charset UTF8 = Charset.forName("UTF-8");
    private final FailoverRepository repository;
    private final MushroomsFailoverConfigurationProperties properties;
    private final List<OkHttpClientHealthCheck> healthChecks;

    @Override
    public okhttp3.Response intercept(final Chain chain) throws IOException {
        final okhttp3.Request request = chain.request();
        final Request cacheRequest = getCacheRequest(request);
        final okhttp3.Response response = getRemoteResponse(chain, request);
        if (properties.getExcludes().stream().anyMatch(exclude -> cacheRequest.getPath().matches(exclude))) {
            return response;
        }
        boolean isHealth = healthChecks.stream().allMatch(check -> check.health(response));
        if (isHealth) {
            log.debug("[MUSHROOMS]Refresh cached data for request\n{}", cacheRequest.toString());
            repository.save(cacheRequest, getCacheResponse(response));
            return response;
        }
        final Response resp = repository.get(cacheRequest);
        if (nonNull(resp)) {
            log.debug("[MUSHROOMS]Respond with cached data for request\n{}", cacheRequest.toString());
            return getResponse(request, response, resp);
        }
        return response;
    }

    private Request getCacheRequest(final okhttp3.Request request) {
        return Request.builder()
                .path(substringBefore(request.url().toString(), "?"))
                .headers(getHeaders(request))
                .method(request.method())
                .body(Optional.ofNullable(request.body()).map(this::getRequestBody).orElse(""))
                .contentType(request.body().contentType().toString())
                .build();
    }

    private Response getCacheResponse(final okhttp3.Response response) {
        return Response.builder().headers(response.headers().names().stream()
                .map(name -> Header.builder().name(name).value(response.headers().get(name)).build())
                .collect(toList()))
                .body(getResponseBody(response.body()))
                .contentType(response.body().contentType().toString())
                .build();
    }

    private okhttp3.Response getResponse(final okhttp3.Request request, final okhttp3.Response response, final Response cacheResponse) {
        return Mono.just(new okhttp3.Response.Builder())
                .map(resp -> resp.code(OK.value()))
                .map(resp -> resp.request(request))
                .map(resp -> resp.message(response.message()))
                .map(resp -> resp.protocol(response.protocol()))
                .map(resp -> resp.body(ResponseBody.create(MediaType.parse(cacheResponse.getContentType()), cacheResponse.getBody())))
                .map(resp -> resp.headers(Headers.of(cacheResponse.getHeaders().stream()
                        .collect(toMap(Header::getName, Header::getValue)))))
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
                    .body(ResponseBody.create(request.body().contentType(), e.getMessage()))
                    .build();
        }
    }

    private List<Header> getHeaders(final okhttp3.Request request) {
        final List<Header> headers = request.headers().names().stream()
                .map(name -> Header.builder().name(name).value(request.headers().get(name)).build())
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
            return buffer.clone().readString(charset);
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

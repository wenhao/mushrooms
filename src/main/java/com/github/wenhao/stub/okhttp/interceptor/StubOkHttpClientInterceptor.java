package com.github.wenhao.stub.okhttp.interceptor;

import com.github.wenhao.common.domain.Header;
import com.github.wenhao.common.domain.Request;
import com.github.wenhao.stub.domain.Stub;
import com.github.wenhao.stub.matcher.RequestMatcher;
import com.github.wenhao.stub.okhttp.health.OkHttpClientHealthCheck;
import com.github.wenhao.stub.properties.MushroomsStubConfigurationProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import okhttp3.Interceptor;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import okio.Buffer;
import reactor.core.publisher.Mono;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.List;
import java.util.Optional;

import static java.util.stream.Collectors.toList;
import static okhttp3.Protocol.HTTP_1_1;
import static org.apache.commons.lang.StringUtils.substringBefore;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;
import static org.springframework.http.HttpStatus.OK;

@Slf4j
@RequiredArgsConstructor
public class StubOkHttpClientInterceptor implements Interceptor {

    private static final Charset UTF8 = Charset.forName("UTF-8");
    private final MushroomsStubConfigurationProperties configuration;
    private final List<RequestMatcher> requestMatchers;
    private final List<OkHttpClientHealthCheck> healthChecks;

    @Override
    public okhttp3.Response intercept(final Chain chain) throws IOException {
        final okhttp3.Request request = chain.request();
        final Request realRequest = getRequest(request);
        final Optional<Stub> optionalStub = configuration.getStubs().stream()
                .filter(stub -> requestMatchers.stream().allMatch(matcher -> matcher.match(stub.getRequest(), realRequest)))
                .findFirst();
        if (optionalStub.isPresent()) {
            if (configuration.isFailover()) {
                final okhttp3.Response response = getRemoteResponse(chain, request);
                boolean isHealth = healthChecks.stream().allMatch(check -> check.health(response));
                if (isHealth) {
                    return response;
                }
            }
            log.debug("[MUSHROOMS]Respond with stub data for request\n{}", realRequest.toString());
            return getResponse(request, optionalStub.get().getResponse());
        }
        return chain.proceed(request);
    }

    private Request getRequest(final okhttp3.Request request) {
        return Request.builder()
                .path(substringBefore(request.url().toString(), "?"))
                .method(request.method())
                .headers(request.headers().names().stream()
                        .map(name -> Header.builder().name(name).value(request.headers().get(name)).build())
                        .collect(toList()))
                .body(Optional.ofNullable(request.body()).map(this::getRequestBody).orElse(""))
                .contentType(request.body().contentType().toString())
                .build();
    }

    private okhttp3.Response getResponse(final okhttp3.Request request, final String body) {
        return Mono.just(new okhttp3.Response.Builder())
                .map(resp -> resp.code(OK.value()))
                .map(resp -> resp.request(request))
                .map(resp -> resp.message("[MUSHROOMS]Respond with stub data"))
                .map(resp -> resp.protocol(HTTP_1_1))
                .map(resp -> resp.body(ResponseBody.create(request.body().contentType(), body)))
                .block()
                .build();
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
}

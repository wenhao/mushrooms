package com.github.wenhao.stub.okhttp.interceptor;

import com.github.wenhao.common.domain.Request;
import com.github.wenhao.common.domain.Response;
import com.github.wenhao.stub.dataloader.DataLoader;
import com.github.wenhao.stub.dataloader.ResourceReader;
import com.github.wenhao.stub.domain.Stub;
import com.github.wenhao.stub.properties.MushroomsStubConfigurationProperties;
import com.google.gson.JsonParser;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import okhttp3.Headers;
import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import okio.Buffer;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.List;
import java.util.Optional;

import static okhttp3.Protocol.HTTP_1_1;
import static org.springframework.http.HttpStatus.OK;

@Slf4j
@RequiredArgsConstructor
public class StubOkHttpClientInterceptor implements Interceptor {

    private static final MediaType APPLICATION_JSON_UTF8 = MediaType.parse("application/json;charset=UTF-8");
    private static final Charset UTF8 = Charset.forName("UTF-8");
    private final MushroomsStubConfigurationProperties properties;
    private final List<DataLoader> dataLoaders;
    private final ResourceReader resourceReader;
    private final JsonParser jsonParser = new JsonParser();

    @Override
    public okhttp3.Response intercept(final Chain chain) throws IOException {
        okhttp3.Request request = chain.request();

        final Request stubRequest = Request.builder()
                .uri(request.url().uri())
                .method(request.method())
                .body(Optional.ofNullable(request.body()).map(this::getRequestBody).orElse(""))
                .build();

        final Optional<Stub> stubOptional = properties.getOkhttp().getStubs().stream()
                .filter(stub -> stubRequest.getUrlButParameters().endsWith(stub.getUri()) &&
                        stubRequest.getMethod().equalsIgnoreCase(stub.getMethod()) &&
                        stubRequest.getBody().equals(resourceReader.readAsString(stub.getBody())))
                .findFirst();

        if (!stubOptional.isPresent()) {
            return chain.proceed(request);
        }

        final Stub stub = stubOptional.get();
        final Response response = Flux.fromIterable(dataLoaders)
                .filter(dataLoader -> dataLoader.isApplicable(stub))
                .next()
                .map(dataLoader -> dataLoader.load(stub))
                .map(body -> Response.builder().body(body).build())
                .block();

        return Optional.ofNullable(response)
                .map(resp -> {
                    log.debug("[MUSHROOMS]Respond with stub data for request\n{}.", stubRequest.toString());
                    return resp;
                })
                .map(resp -> getResponse(request, resp))
                .orElse(getResponse(request, Response.empty()));
    }

    private okhttp3.Response getResponse(final okhttp3.Request request, final Response response) {
        return Mono.just(new okhttp3.Response.Builder())
                .map(resp -> resp.code(OK.value()))
                .map(resp -> resp.request(request))
                .map(resp -> resp.message("[MUSHROOMS]Respond with stub data"))
                .map(resp -> resp.protocol(HTTP_1_1))
                .map(resp -> resp.body(ResponseBody.create(APPLICATION_JSON_UTF8, response.getBody())))
                .map(resp -> resp.headers(Headers.of("Content-Type", APPLICATION_JSON_UTF8.toString())))
                .block()
                .build();
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
}

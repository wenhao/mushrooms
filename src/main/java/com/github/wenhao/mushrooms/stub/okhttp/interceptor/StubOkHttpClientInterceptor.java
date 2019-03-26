package com.github.wenhao.mushrooms.stub.okhttp.interceptor;

import com.github.wenhao.mushrooms.stub.config.StubConfiguration;
import com.github.wenhao.mushrooms.stub.domain.Header;
import com.github.wenhao.mushrooms.stub.domain.Parameter;
import com.github.wenhao.mushrooms.stub.domain.Request;
import com.github.wenhao.mushrooms.stub.domain.Stub;
import com.github.wenhao.mushrooms.stub.matcher.BodyMatcher;
import com.github.wenhao.mushrooms.stub.matcher.HeaderMatcher;
import com.github.wenhao.mushrooms.stub.matcher.JsonBodyMatcher;
import com.github.wenhao.mushrooms.stub.matcher.JsonPathMatcher;
import com.github.wenhao.mushrooms.stub.matcher.MethodMatcher;
import com.github.wenhao.mushrooms.stub.matcher.ParameterMatcher;
import com.github.wenhao.mushrooms.stub.matcher.PathMatcher;
import com.github.wenhao.mushrooms.stub.matcher.RequestBodyMatcher;
import com.github.wenhao.mushrooms.stub.matcher.RequestMatcher;
import com.github.wenhao.mushrooms.stub.matcher.XMLBodyMatcher;
import com.github.wenhao.mushrooms.stub.matcher.XpathBodyMatcher;
import com.github.wenhao.mushrooms.stub.okhttp.health.HttpStatusOkHttpClientHealthCheck;
import com.github.wenhao.mushrooms.stub.okhttp.health.OkHttpClientHealthCheck;
import static java.util.stream.Collectors.toList;
import lombok.AllArgsConstructor;
import okhttp3.Interceptor;
import static okhttp3.Protocol.HTTP_1_1;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import okio.Buffer;
import static org.apache.commons.lang3.StringUtils.substringAfter;
import static org.apache.commons.lang3.StringUtils.substringBefore;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Pattern;

@AllArgsConstructor
public class StubOkHttpClientInterceptor implements Interceptor {

    private static final Charset UTF8 = Charset.forName("UTF-8");
    private final StubConfiguration configuration;
    private final List<RequestMatcher> requestMatchers;
    private final List<OkHttpClientHealthCheck> healthChecks;
    private final Logger logger = Logger.getLogger(StubOkHttpClientInterceptor.class.getName());

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
                .parameters(getParameters(request))
                .body(Optional.ofNullable(request.body()).map(this::getRequestBody).orElse(""))
                .contentType(request.body().contentType().toString())
                .build();
    }

    private List<Parameter> getParameters(okhttp3.Request request) {
        String parameterString = substringAfter(request.url().toString(), "?");
        return Pattern.compile("&")
                .splitAsStream(parameterString)
                .map(param -> new Parameter(substringBefore(param, "="), substringAfter(param, "=")))
                .collect(toList());
    }

    private okhttp3.Response getResponse(final okhttp3.Request request, final String body) {
        return new okhttp3.Response.Builder()
                .code(200)
                .request(request)
                .message("[MUSHROOMS]Respond with stub data")
                .protocol(HTTP_1_1)
                .addHeader("Content-Type", request.body().contentType().toString())
                .body(ResponseBody.create(request.body().contentType(), body))
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
            logger.log(Level.SEVERE, e.getMessage(), e);
            return new okhttp3.Response.Builder()
                    .code(500)
                    .request(request)
                    .message(e.getMessage())
                    .protocol(HTTP_1_1)
                    .body(ResponseBody.create(request.body().contentType(), e.getMessage()))
                    .build();
        }
    }

    public static StubOkHttpClientInterceptorBuilder builder() {
        return new StubOkHttpClientInterceptorBuilder();
    }

    public static class StubOkHttpClientInterceptorBuilder {

        private StubConfiguration configuration;
        private List<RequestMatcher> requestMatchers;
        private List<OkHttpClientHealthCheck> healthChecks;

        public StubOkHttpClientInterceptorBuilder requestMatchers(List<RequestMatcher> requestMatchers) {
            this.requestMatchers = requestMatchers;
            return this;
        }

        public StubOkHttpClientInterceptorBuilder healthChecks(List<OkHttpClientHealthCheck> healthChecks) {
            this.healthChecks = healthChecks;
            return this;
        }

        public StubOkHttpClientInterceptorBuilder configuration(StubConfiguration configuration) {
            this.configuration = configuration;
            return this;
        }

        private List<RequestMatcher> defaultRequestMatchers() {
            List<RequestBodyMatcher> requestBodyMatchers = new ArrayList<>(4);
            requestBodyMatchers.add(new JsonBodyMatcher());
            requestBodyMatchers.add(new JsonPathMatcher());
            requestBodyMatchers.add(new XMLBodyMatcher());
            requestBodyMatchers.add(new XpathBodyMatcher());
            BodyMatcher bodyMatcher = new BodyMatcher(requestBodyMatchers);
            List<RequestMatcher> requestMatchers = new ArrayList<>(4);
            requestMatchers.add(new PathMatcher());
            requestMatchers.add(new ParameterMatcher());
            requestMatchers.add(new MethodMatcher());
            requestMatchers.add(new HeaderMatcher());
            requestMatchers.add(bodyMatcher);
            return requestMatchers;
        }

        private List<OkHttpClientHealthCheck> defaultHealthChecks() {
            List<OkHttpClientHealthCheck> healthChecks = new ArrayList<>();
            healthChecks.add(new HttpStatusOkHttpClientHealthCheck());
            return healthChecks;
        }

        public StubOkHttpClientInterceptor build() {
            this.requestMatchers = Optional.ofNullable(requestMatchers).orElse(defaultRequestMatchers());
            this.healthChecks = Optional.ofNullable(healthChecks).orElse(defaultHealthChecks());
            return new StubOkHttpClientInterceptor(this.configuration, this.requestMatchers, this.healthChecks);
        }
    }
}

package com.github.wenhao.repository;

import com.github.wenhao.config.ParrotConfiguration;
import com.github.wenhao.domain.Cookie;
import com.github.wenhao.domain.Header;
import com.github.wenhao.domain.HttpRequest;
import com.github.wenhao.domain.HttpResponse;
import com.github.wenhao.domain.Parameter;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.stereotype.Repository;
import org.springframework.web.util.ContentCachingResponseWrapper;
import reactor.core.publisher.Mono;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static java.util.Collections.emptyList;
import static java.util.stream.Collectors.toList;
import static org.springframework.util.CollectionUtils.isEmpty;

@Repository
@RequiredArgsConstructor
public class ParrotCacheRepository {
    private static final String CACHE = "CACHE";
    private final HashOperations<String, HttpRequest, HttpResponse> cacheHashOperations;
    private final ParrotConfiguration parrotConfiguration;

    public void save(HttpServletRequest request, HttpServletResponse response) {
        final HttpRequest httpRequest = getHttpRequest(request);
        cacheHashOperations.put(parrotConfiguration.getKey(), httpRequest, getHttpResponse(response));
    }

    public HttpResponse get(HttpServletRequest request) {
        final HttpRequest httpRequest = getHttpRequest(request);
        return cacheHashOperations.get(parrotConfiguration.getKey(), httpRequest);
    }

    private HttpResponse getHttpResponse(HttpServletResponse response) {
        return Mono.just(response)
                .map(ContentCachingResponseWrapper::new)
                .map(item -> new HttpResponse(new String(item.getContentAsByteArray())))
                .block();
    }

    private HttpRequest getHttpRequest(final HttpServletRequest request) {
        return HttpRequest.builder()
                .path(request.getRequestURI())
                .method(request.getMethod())
                .headers(getHeaders(request))
                .parameters(getParameters(request))
                .cookies(getCookies(request))
                .body(getBody(request))
                .build();
    }

    private String getBody(HttpServletRequest request) {
        return Optional.ofNullable(request.getMethod()).filter(
                "POST"::equalsIgnoreCase).map(method -> getBodyString(request)).orElse("");
    }

    private List<Parameter> getParameters(HttpServletRequest request) {
        return request.getParameterMap().entrySet().stream().map(
                key -> Parameter.builder().name(key.getKey()).values(Arrays.stream(request.getParameterValues(key.getKey()))
                        .collect(toList())).build()).collect(toList());
    }

    private List<Cookie> getCookies(HttpServletRequest request) {
        final List<Cookie> cookies = Optional.ofNullable(request.getCookies())
                .map(items -> Arrays.stream(request.getCookies())
                        .map(cookie -> Cookie.builder().name(cookie.getName()).value(cookie.getValue()).build())
                        .collect(toList())).orElse(emptyList());
        if (!isEmpty(parrotConfiguration.getCookies())) {
            return cookies.stream()
                    .filter(cookie -> parrotConfiguration.getCookies().contains(cookie.getName()))
                    .collect(toList());
        }
        return cookies;
    }

    private List<Header> getHeaders(HttpServletRequest request) {
        final List<Header> headers = Collections.list(request.getHeaderNames()).stream()
                .map(name -> Header.builder().name(name).values(Collections.list(request.getHeaders(name))).build())
                .collect(toList());
        if (!isEmpty(parrotConfiguration.getHeaders())) {
            return headers.stream()
                    .filter(header -> parrotConfiguration.getHeaders().contains(header.getName()))
                    .collect(toList());
        }
        return headers;
    }

    private String getBodyString(HttpServletRequest request) {
        try {
            return request.getReader().lines().collect(Collectors.joining(System.lineSeparator()));
        } catch (IOException e) {
            return "";
        }
    }
}

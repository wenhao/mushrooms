package com.github.wenhao.failover.resttemplate.interceptor;

import com.github.wenhao.common.domain.Header;
import com.github.wenhao.common.domain.Request;
import com.github.wenhao.common.domain.Response;
import com.github.wenhao.failover.properties.MushroomsFailoverConfigurationProperties;
import com.github.wenhao.failover.repository.FailoverRepository;
import com.github.wenhao.failover.resttemplate.health.RestTemplateHealthCheck;
import com.github.wenhao.failover.resttemplate.response.CachedClientHttpResponse;
import com.github.wenhao.failover.resttemplate.response.ClientHttpResponseWrapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toMap;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;
import static org.springframework.http.HttpStatus.OK;

@Slf4j
@RequiredArgsConstructor
public class CachingRestTemplateInterceptor implements ClientHttpRequestInterceptor {

    private final FailoverRepository repository;
    private final MushroomsFailoverConfigurationProperties properties;
    private final List<RestTemplateHealthCheck> healthChecks;

    @Override
    public ClientHttpResponse intercept(final HttpRequest request, final byte[] body, final ClientHttpRequestExecution execution) throws IOException {
        ClientHttpResponse response = getRemoteResponse(request, body, execution);
        ClientHttpResponseWrapper responseWrapper = new ClientHttpResponseWrapper(response);
        Request cacheRequest = Request.builder()
                .uri(request.getURI())
                .method(request.getMethodValue())
                .headers(getHeaders(request))
                .body(new String(body))
                .build();
        final boolean isExcluded = properties.getExcludes().stream().anyMatch(exclude -> cacheRequest.getUrlButParameters().endsWith(exclude));
        if (isExcluded) {
            return response;
        }
        boolean isHealth = healthChecks.stream().allMatch(restTemplateHealthCheck -> restTemplateHealthCheck.health(responseWrapper));
        if (isHealth) {
            log.debug("[MUSHROOMS]Refresh cached data for request\n{}.", cacheRequest.toString());
            final Response cacheResponse = Response.builder()
                    .body(responseWrapper.getBodyAsString())
                    .headers(responseWrapper.getHeaders().entrySet().stream()
                            .map(entry -> Header.builder().name(entry.getKey()).values(entry.getValue()).build())
                            .collect(toList()))
                    .build();
            repository.save(cacheRequest, cacheResponse);
            return responseWrapper;
        }
        return Optional.ofNullable(repository.get(cacheRequest))
                .map(resp -> {
                    log.debug("[MUSHROOMS]Respond with cached data for request\n{}.", cacheRequest.toString());
                    log.debug("[MUSHROOMS]Respond with cached data for error response\n{}.", responseWrapper.getBodyAsString());
                    return resp;
                })
                .map(resp -> CachedClientHttpResponse.builder()
                        .httpStatus(OK)
                        .httpHeaders(getHttpHeaders(resp))
                        .body(resp.getBody())
                        .build())
                .orElse(CachedClientHttpResponse.builder()
                        .httpStatus(responseWrapper.getStatusCode())
                        .httpHeaders(responseWrapper.getHeaders())
                        .body(responseWrapper.getBodyAsString())
                        .build());
    }

    private ClientHttpResponse getRemoteResponse(HttpRequest request, byte[] body, ClientHttpRequestExecution execution) {
        try {
            return execution.execute(request, body);
        } catch (Exception e) {
            return CachedClientHttpResponse.builder()
                    .httpStatus(INTERNAL_SERVER_ERROR)
                    .httpHeaders(request.getHeaders())
                    .body("")
                    .build();
        }
    }

    private HttpHeaders getHttpHeaders(final Response resp) {
        final HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.putAll(resp.getHeaders().stream().collect(toMap(Header::getName, Header::getValues)));
        return httpHeaders;
    }

    private List<Header> getHeaders(final HttpRequest request) {
        List<Header> headers = request.getHeaders().keySet().stream()
                .map(key -> Header.builder().name(key).values(request.getHeaders().get(key)).build())
                .collect(toList());
        return headers.stream()
                .filter(header -> properties.getHeaders().contains(header.getName()))
                .collect(toList());
    }
}

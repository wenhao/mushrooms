package com.github.wenhao.resttemplate.interceptor;

import com.github.wenhao.common.config.CachingConfigurationProperties;
import com.github.wenhao.common.domain.Request;
import com.github.wenhao.common.domain.Header;
import com.github.wenhao.resttemplate.health.RestTemplateHealthCheck;
import com.github.wenhao.common.repository.CachingRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

import static java.util.stream.Collectors.toList;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;
import static org.springframework.http.HttpStatus.OK;
import static org.springframework.util.CollectionUtils.isEmpty;

@Slf4j
@Component
@RequiredArgsConstructor
public class CachingRestTemplateInterceptor implements ClientHttpRequestInterceptor {
    private final CachingRepository cachingRepository;
    private final CachingConfigurationProperties cachingConfigurationProperties;
    private final List<RestTemplateHealthCheck> restTemplateHealthChecks;

    @Override
    public ClientHttpResponse intercept(final HttpRequest request, final byte[] body, final ClientHttpRequestExecution execution) throws IOException {
        ClientHttpResponse response = getRealResponse(request, body, execution);
        ClientHttpResponseWrapper responseWrapper = new ClientHttpResponseWrapper(response);
        Request cacheRequest = Request.builder()
                .uri(request.getURI())
                .method(request.getMethodValue())
                .headers(getHeaders(request))
                .body(new String(body))
                .build();
        boolean isHealth = restTemplateHealthChecks.stream().allMatch(restTemplateHealthCheck -> restTemplateHealthCheck.health(responseWrapper));
        if (isHealth) {
            log.info("[PARROT]Refresh cached data for {}.", cacheRequest.toString());
            cachingRepository.save(cacheRequest, responseWrapper.getBodyAsString());
            return responseWrapper;
        }
        log.info("[PARROT]Respond with cached data for {}.", cacheRequest.toString());
        return Optional.ofNullable(cachingRepository.get(cacheRequest))
                .map(item -> CachedClientHttpResponse.builder()
                        .httpStatus(OK)
                        .httpHeaders(response.getHeaders())
                        .body(item)
                        .build())
                .orElse(new CachedClientHttpResponse(response));
    }

    private ClientHttpResponse getRealResponse(HttpRequest request, byte[] body, ClientHttpRequestExecution execution) {
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

    private List<Header> getHeaders(final HttpRequest request) {
        List<Header> headers = request.getHeaders().keySet().stream()
                .map(key -> Header.builder().name(key).values(request.getHeaders().get(key)).build())
                .collect(toList());
        if (!isEmpty(cachingConfigurationProperties.getHeaders())) {
            return headers.stream()
                    .filter(header -> cachingConfigurationProperties.getHeaders().contains(header.getName()))
                    .collect(toList());
        }
        return headers;
    }
}

package com.github.wenhao.failover.resttemplate.config;

import java.util.List;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.web.client.RestTemplate;

@RequiredArgsConstructor
public class CachingRestTemplatePostProcessor implements BeanPostProcessor {

    private final List<ClientHttpRequestInterceptor> interceptors;

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        if (bean instanceof RestTemplate) {
            final RestTemplate restTemplate = (RestTemplate) bean;
            List<ClientHttpRequestInterceptor> interceptors = restTemplate.getInterceptors();
            interceptors.addAll(interceptors);
            restTemplate.setInterceptors(interceptors);
            return restTemplate;
        }
        return bean;
    }
}

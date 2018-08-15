package com.github.wenhao.failover.resttemplate.config;

import com.github.wenhao.failover.resttemplate.interceptor.CachingRestTemplateInterceptor;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@RequiredArgsConstructor
public class CachingRestTemplatePostProcessor implements BeanPostProcessor {

    private final CachingRestTemplateInterceptor interceptor;

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        if (bean instanceof RestTemplate) {
            final RestTemplate restTemplate = (RestTemplate) bean;
            List<ClientHttpRequestInterceptor> interceptors = restTemplate.getInterceptors();
            interceptors.add(interceptor);
            restTemplate.setInterceptors(interceptors);
            return restTemplate;
        }
        return bean;
    }
}

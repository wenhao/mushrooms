package com.github.wenhao.resttemplate.config;

import com.github.wenhao.resttemplate.interceptor.CachingRestTemplateInterceptor;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Configuration
@ConditionalOnProperty(value = "parrot.resttemplate.enabled", havingValue = "true")
@RequiredArgsConstructor
public class CachingRestTemplatePostProcessor implements BeanPostProcessor {

    private final CachingRestTemplateInterceptor cachingRestTemplateInterceptor;

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        if (bean instanceof RestTemplate) {
            final RestTemplate restTemplate = (RestTemplate) bean;
            List<ClientHttpRequestInterceptor> interceptors = restTemplate.getInterceptors();
            interceptors.add(cachingRestTemplateInterceptor);
            restTemplate.setInterceptors(interceptors);
            return restTemplate;
        }
        return bean;
    }
}

package com.github.wenhao.config;

import com.github.wenhao.interceptor.RestTemplateInterceptor;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Configuration
@ConditionalOnProperty(value = "parrot.enabled", havingValue = "true", matchIfMissing = true)
@RequiredArgsConstructor
public class RestTemplatePostProcessor implements BeanPostProcessor {

    private final RestTemplateInterceptor restTemplateInterceptor;

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        if (bean instanceof RestTemplate) {
            final RestTemplate restTemplate = (RestTemplate) bean;
            List<ClientHttpRequestInterceptor> interceptors = restTemplate.getInterceptors();
            interceptors.add(restTemplateInterceptor);
            restTemplate.setInterceptors(interceptors);
            return restTemplate;
        }
        return bean;
    }
}

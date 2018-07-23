package com.github.wenhao.config;

import lombok.AllArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@EnableWebMvc
@AllArgsConstructor
@ConditionalOnProperty(value = "parrot.cache.enabled", havingValue = "true", matchIfMissing = true)
public class ParrotWebMvcConfig implements WebMvcConfigurer {

    private ParrotHttpInterceptor parrotHttpInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(parrotHttpInterceptor).addPathPatterns("/**");
    }
}

package com.github.wenhao.config;

import com.github.wenhao.domain.HttpResponse;
import com.github.wenhao.repository.ParrotCacheRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.PrintWriter;

import static java.util.Objects.nonNull;
import static org.springframework.http.HttpStatus.OK;

@Service
@RequiredArgsConstructor
@ConditionalOnProperty(value = "parrot.enabled", havingValue = "true", matchIfMissing = true)
public class ParrotHttpInterceptor extends HandlerInterceptorAdapter {

    private final ParrotCacheRepository parrotCacheRepository;

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler,
                           ModelAndView modelAndView) throws Exception {
        if (HttpStatus.valueOf(response.getStatus()).equals(OK)) {
            response.addHeader("Cached-Data", "false");
            parrotCacheRepository.save(request, response);
            return;
        }
        HttpResponse cachedResponse = parrotCacheRepository.get(request);
        if (nonNull(cachedResponse)) {
            response.addHeader("Cached-Data", "true");
            response.setStatus(200);
            PrintWriter printWriter = response.getWriter();
            printWriter.write(cachedResponse.getBody());
            printWriter.flush();
            printWriter.close();
        }
    }

}

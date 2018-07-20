package com.github.wenhao.config;

import static org.apache.commons.lang3.StringUtils.EMPTY;
import static org.springframework.http.HttpStatus.OK;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.mockserver.model.Cookie;
import org.mockserver.model.Header;
import org.mockserver.model.HttpRequest;
import org.mockserver.model.Parameter;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;
import reactor.core.publisher.Mono;

@Service
public class HttpCacheInterceptor extends HandlerInterceptorAdapter {

  @Override
  public boolean preHandle(HttpServletRequest request, HttpServletResponse response,
      Object handler) throws Exception {
    return true;
  }

  @Override
  public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler,
      ModelAndView modelAndView) throws Exception {
    HttpRequest httpRequest = Mono.just(new HttpRequest())
        .map(item -> item.withPath(request.getRequestURI()))
        .map(item -> item.withMethod(request.getMethod()))
        .map(item -> item.withHeaders(getHeaders(request)))
        .map(item -> item.withCookies(getCookies(request)))
        .map(item -> item.withQueryStringParameters(getParameters(request)))
        .map(item -> item.withBody(getBody(request)))
        .block();
    if (HttpStatus.valueOf(response.getStatus()).equals(OK)) {
      response.addHeader("Cached-Data", "false");
      return;
    }
    response.addHeader("Cached-Data", "true");
    response.setStatus(OK.value());
    PrintWriter body = response.getWriter();
    body.write("getFromCache");
    body.flush();
    body.close();
  }

  private String getBody(HttpServletRequest request) {
    return Optional.ofNullable(request.getMethod()).filter(
        "POST"::equalsIgnoreCase).map(method -> getBodyString(request)).orElse(EMPTY);
  }

  private List<Parameter> getParameters(HttpServletRequest request) {
    return request.getParameterMap().entrySet().stream().map(
        key -> Parameter.param(key.getKey(),
            Arrays.stream(request.getParameterValues(key.getKey()))
                .collect(Collectors.toList()))).collect(Collectors.toList());
  }

  private List<Cookie> getCookies(HttpServletRequest request) {
    return Optional.ofNullable(request.getCookies())
        .map(cookies -> Arrays.stream(request.getCookies())
            .map(cookie -> Cookie.cookie(cookie.getName(), cookie.getValue()))
            .collect(Collectors.toList())).orElse(Collections.emptyList());
  }

  private List<Header> getHeaders(HttpServletRequest request) {
    return Collections.list(request.getHeaderNames()).stream()
        .map(name -> Header.header(name, Collections.list(request.getHeaders(name))))
        .collect(Collectors.toList());
  }

  private String getBodyString(HttpServletRequest request) {
    try {
      return request.getReader().lines().collect(Collectors.joining(System.lineSeparator()));
    } catch (IOException e) {
      return EMPTY;
    }
  }
}

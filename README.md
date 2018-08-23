[![Build Status](https://travis-ci.com/wenhao/mushrooms.svg?branch=master)](https://travis-ci.com/wenhao/mushrooms)
[![Coverage Status](https://coveralls.io/repos/github/wenhao/mushrooms/badge.svg?branch=master)](https://coveralls.io/github/wenhao/mushrooms?branch=master)
[![Apache 2.0 License](https://img.shields.io/badge/license-Apache%202-blue.svg)](https://www.apache.org/licenses/LICENSE-2.0.txt)

![Mushrooms][logo]

# Mushrooms

Mushrooms is an easy setup failover and stub framework. To ensure high levels of efficiency for remote service integration.

## Why

Remote service integration, especially based on HTTP protocol, e.g. web service, REST etc, highly unstable when developing.

### Features

##### Failover

Failover feature rely on okhhtp3, make sure FeignClient/RestTemplate are using okhttp3.

* FeignClient/RestTemplate respond with cached data when remote server failure.

##### Stub

* Stub REST API.
* Stub Soap API.

### Gradle

```groovy
repositories {
    jcenter()
}

dependencies {
    compile 'com.github.wenhao:mushrooms:2.3.0'
}
```

### Maven

```xml
<dependency>
    <groupId>com.github.wenhao</groupId>
    <artifactId>mushrooms</artifactId>
    <version>2.3.0</version>
</dependency>
```

### Build

```
./gradlew clean build
```

### Get Started

#### Failover Configuration

##### application.yml

Enabled mushrooms failover and set included headers, don't include any frequent changeable header.

```yaml
mushrooms:
  failover:
    enabled: true
    headers:
      - application-specific
      - content-type
```

Enabled RestTemplate failover, Customize RestTemplate by using Okhttp3.

```java
@Configuration
public class RestTemplateConfiguration {

  @Bean
  public RestTemplate restTemplate(ClientHttpRequestFactory clientHttpRequestFactory) {
    return new RestTemplate(clientHttpRequestFactory);
  }

  @Bean
  public ClientHttpRequestFactory okHttp3ClientHttpRequestFactory(OkHttpClient okHttpClient) {
    return new OkHttp3ClientHttpRequestFactory(okHttpClient);
  }
}
```

#### Stub Configuration

Enabled mushrooms stub and set stub request and response.

**Stub REST API**
```yaml
mushrooms:
  stub:
    enabled: true
    stubs:
      - request:
          path: ${REAL_HOST:http://localhost:8080}/stub
          method: POST
          body: /stubs/stub_rest_request.json
        response: /stubs/stub_rest_response.json
```

**Stub Soap API**
```yaml
mushrooms:
  stub:
    enabled: true
    stubs:
      - request:
          path: ${REAL_HOST:http://localhost:8080}/stub/get_book
          method: POST
          body: /stubs/stub_soap_request.xml
        response: /stubs/stub_soap_response.xml
```

**Request Matchers**

A **request matcher** can contain any of the following matchers:

* method - string value as a plain text, regular expression.
* path - string value as a plain text, regular expression.
* query string - key to multiple values as a plain text, regular expression.
* headers - key to multiple values as a plain text, regular expression.
* cookies - key to value as a plain text, regular expression.
* body
    * XPath
    * XML - full or partial match. 
    * JSON - full or partial match. 
    * regular expression
    * plain text (i.e. exact match)
    
**Full setup**
```yaml
mushrooms:
  stub:
    enabled: true
    stubs:
      - request:
          path: ${REAL_HOST:http://localhost:8080}/stub(.*)
          parameters: 
            - key: [A-z]{0,10}
              value: [A-Z0-9]+
          method: P(.*)
          headers:
            - key: [A-z]{0,10}
              value: [A-Z0-9]+
          cookies:
            - key: [A-z]{0,10}
              value: [A-Z0-9]+    
          body: /stubs/stub_rest_request.json
        response: /stubs/stub_rest_response.json
```    

#### Generic Configuration

If enabled okhttp failover or stub, enabling feign okhttp client.
```yaml
feign:
  okhttp:
    enabled: true
  client:
    config:
      default:
        connectTimeout: 5000
        readTimeout: 5000
        loggerLevel: full
```

Failover will use redis, if RedisTemplate bean is not configured, add follow configuration:
```yaml
spring:
  redis:
    host: localhost
    port: 6379
    password:
    lettuce:
      pool:
        max-active: 8
        max-idle: 8
        max-wait: -1ms
        min-idle: 1
```

Logging
```yaml
logging:
  level:
    com.github.wenhao: DEBUG
```

#### Customization

As default, failover only applys if httpstatus not equals to 200.

##### Custom OkHttp Health Check

As default, [HttpStatusOkHttpClientHealthCheck.java] added, customize health checks if need:

```java
@Component
public class CustomOkHttpClientHealthCheck implements OkHttpClientHealthCheck {

    @Override
    public boolean health(final Response response) {
        final String body = getResponseBody(response);
        try {
            final JSONObject jsonObject = new JSONObject(body);
            return jsonObject.getBoolean("success");
        } catch (Exception e) {
            return false;
        }
    }
}
```

#### Attentions

1. Stub okhttp interceptor prior to failover okhttp interceptor.
2. Exclude from failover okhttp configuration if don't need it when stub.

### Copyright and license

Copyright © 2018 Wen Hao

Licensed under [Apache License]

[logo]: ./docs/images/logo.png
[HttpStatusRestTemplateHealthCheck.java]: ./src/main/java/com/github/wenhao/failover/resttemplate/health/HttpStatusRestTemplateHealthCheck.java
[HttpStatusOkHttpClientHealthCheck.java]: ./src/main/java/com/github/wenhao/failover/okhttp/health/HttpStatusOkHttpClientHealthCheck.java
[Apache License]: ./LICENSE
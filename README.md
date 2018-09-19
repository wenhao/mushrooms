[![Build Status](https://travis-ci.com/wenhao/mushrooms.svg?branch=master)](https://travis-ci.com/wenhao/mushrooms)
[![Coverage Status](https://coveralls.io/repos/github/wenhao/mushrooms/badge.svg?branch=master)](https://coveralls.io/github/wenhao/mushrooms?branch=master)
[![Apache 2.0 License](https://img.shields.io/badge/license-Apache%202-blue.svg)](https://www.apache.org/licenses/LICENSE-2.0.txt)

![Mushrooms][logo]

# Mushrooms

Mushrooms is an easy setup mock or stub framework. To ensure high levels of efficiency for remote service integration.

## Why

Remote service integration, especially based on HTTP protocol, e.g. web service, REST etc, highly unstable when developing.

### Features

##### Stub

Stub feature rely on spring boot and okhttp3, make sure FeignClient/RestTemplate are using okhttp3.

* Stub REST API.
* Stub Soap API.

### Gradle

```groovy
repositories {
    jcenter()
}

dependencies {
    compile 'com.github.wenhao:mushrooms:3.0.4'
}
```

### Maven

```xml
<dependency>
    <groupId>com.github.wenhao</groupId>
    <artifactId>mushrooms</artifactId>
    <version>3.0.4</version>
</dependency>
```

### Build

```
./gradlew clean build
```

### Get Started

**Request Matchers**

A **request matcher** can contain any of the following matchers:

* method - string value as a plain text, regular expression.
* path - string value as a plain text, regular expression.
* query string - key to multiple values as a plain text, regular expression.
* headers - key to multiple values as a plain text, regular expression.
* body
    * XPath(example, body: xpath:/soap:Envelope/soap:Body/m:GetBookRequest[m:BookName="Java"])
    * XML - full or partial match. 
    * JSON - full or partial match. 
    * JsonPath(example, body: jsonPath:$.store.book[?(@.price < 10)]), [jsonPath syntax](https://github.com/json-path/JsonPath)
    
**Full setup**
```yaml
mushrooms:
  stub:
    enabled: true
    failover: true
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
          body: /stubs/stub_rest_request.json
        response: /stubs/stub_rest_response.json
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

Enabled RestTemplate stub, Customize RestTemplate by using Okhttp3.

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

#### Generic Configuration

If enabled okhttp stub, enabling feign okhttp client.

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

Logging
```yaml
logging:
  level:
    com.github.wenhao: DEBUG
```

#### Failover

As Failover is true, will call real endpoint first and return real response if health.

### Copyright and license

Copyright © 2018 Wen Hao

Licensed under [Apache License]

[logo]: ./docs/images/logo.png
[Apache License]: ./LICENSE
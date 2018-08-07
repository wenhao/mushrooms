[![Build Status](https://travis-ci.com/wenhao/mushrooms.svg?branch=master)](https://travis-ci.com/wenhao/mushrooms)
[![Coverage Status](https://coveralls.io/repos/github/wenhao/mushrooms/badge.svg?branch=master)](https://coveralls.io/github/wenhao/mushrooms?branch=master)
[![Apache 2.0 License](https://img.shields.io/badge/license-Apache%202-blue.svg)](https://www.apache.org/licenses/LICENSE-2.0.txt)

![Mushrooms][logo]

# Mushrooms

Cacheable Remote Call

### Features

##### Failover

* RestTemplate Request Cache.
* Okhttp Request Cache with OpenFeign.

##### Stub

* Stub Okhttp Response, Request and Response Provider as Json File.

### Gradle

```groovy
repositories {
    jcenter()
}

dependencies {
    compile 'com.github.wenhao:mushrooms:2.1.3'
}
```

### Maven

```xml
<dependency>
    <groupId>com.github.wenhao</groupId>
    <artifactId>mushrooms</artifactId>
    <version>2.1.3</version>
</dependency>
```

### Build

```
./gradlew clean build
```

### Get Started

#### Generic Configuration

If enabled okhttp, enabling feign okhttp as well.
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

If RedisTemplate is not configured, add follow configuration:
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

#### Failover Configuration

##### application.yml

Enabled mushrooms tools and set included headers.

```yaml
mushrooms:
  failover:
    okhttp:
      enabled: true
    resttemplate:
      enabled: true
    headers:
      - application-specific
      - content-type
```

##### Custom RestTemplate Health Check

As default, [HttpStatusRestTemplateHealthCheck.java] added, customize health checks:

```java
@Component
public class CustomRestTemplateHealthCheck implements RestTemplateHealthCheck {

    @Override
    public boolean health(final ClientHttpResponseWrapper response) {
        try {
            final JSONObject jsonObject = new JSONObject(response.getBodyAsString());
            return jsonObject.getBoolean("success");
        } catch (JSONException e) {
            return false;
        }
    }
}
```

##### Custom OkHttp Health Check

As default, [HttpStatusOkHttpClientHealthCheck.java] added, customize health checks:

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

#### Stub Configuration

Enabled mushrooms stub tools and set stub request and response.

```yaml
mushrooms:
  stub:
    okhttp:
      enabled: true
      stubs:
        - uri: "http://localhost:8080/stub"
          method: POST
          body: /stubs/stub_request.json
          response: /stubs/stub_response.json
```

#### SpringBootApplication

```java
@SpringBootApplication
public class Application {

  public static void main(String[] args) {
    SpringApplication.run(Application.class, args);
  }
}
```

#### Attentions

1. Stub okhttp interceptor prior to failover okhttp interceptor.
2. exclude from failover okhttp configuration if don't need.

### Copyright and license

Copyright © 2018 Wen Hao

Licensed under [Apache License]

[logo]: ./docs/images/logo.png
[HttpStatusRestTemplateHealthCheck.java]: ./src/main/java/com/github/wenhao/resttemplate/health/HttpStatusRestTemplateHealthCheck.java
[HttpStatusOkHttpClientHealthCheck.java]: ./src/main/java/com/github/wenhao/okhttp/health/HttpStatusOkHttpClientHealthCheck.java
[Apache License]: ./LICENSE
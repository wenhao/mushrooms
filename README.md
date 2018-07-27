[![Build Status](https://travis-ci.com/wenhao/mushrooms.svg?branch=master)](https://travis-ci.com/wenhao/mushrooms)
[![Coverage Status](https://coveralls.io/repos/github/wenhao/mushrooms/badge.svg?branch=master)](https://coveralls.io/github/wenhao/mushrooms?branch=master)
[![Apache 2.0 License](https://img.shields.io/badge/license-Apache%202-blue.svg)](https://www.apache.org/licenses/LICENSE-2.0.txt)

![Mushrooms][logo]

# Mushrooms

Cacheable http server gateway

### Features

* RestTemplate Cache.

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

#### application.yml

Enabled mushrooms tools and set included headers.

```yaml
mushrooms:
  resttemplate:
    enabled: true
  okhttp:
    enabled: true
  key: MUSHROOMS-CACHE
  headers:
    - content-type
    - application-specific
```

If RedisTemplate is not configured, add follow configuration:
```yaml
spring:
  redis:
    host: localhost
    port: 6379
    password: password
    lettuce:
      pool:
        max-active: 8
        max-idle: 8
        max-wait: -1ms
        min-idle: 1
```

#### SpringBootApplication

```java
@SpringBootApplication
@ComponentScan({"com.github.wenhao"})
public class Application {

  public static void main(String[] args) {
    SpringApplication.run(Application.class, args);
  }
}
```

#### Custom RestTemplate Health Check

As default, [HttpStatusRestTemplateHealthCheck.java] added, customize health checks:

```java
@Component
public class CustomRestTemplateHealthCheck implements RestTemplateHealthCheck {

    @Override
    public boolean health(final ClientHttpResponseWrapper response) {
        try {
            final JSONObject jsonObject = new JSONObject(response.getBodyAsString());
            final boolean isSuccess = jsonObject.getBoolean("success");
            return isSuccess;
        } catch (JSONException e) {
            return false;
        }
    }
}
```

#### Custom OkHttp Health Check

As default, [HttpStatusOkHttpClientHealthCheck.java] added, customize health checks:

```java
@Component
public class CustomOkHttpClientHealthCheck implements OkHttpClientHealthCheck {

    private static final Charset UTF8 = Charset.forName("UTF-8");
    
    @Override
    public boolean health(final Response response) {
        final String body = getResponseBody(response.body());
        try {
            final JSONObject jsonObject = new JSONObject(body);
            final boolean isSuccess = jsonObject.getBoolean("success");
            return isSuccess;
        } catch (Exception e) {
            return false;
        }
    }

    private String getResponseBody(final ResponseBody responseBody) {
        try {
            BufferedSource source = responseBody.source();
            source.request(Long.MAX_VALUE);
            Buffer buffer = source.buffer();
            Charset charset = UTF8;
            MediaType contentType = responseBody.contentType();
            if (contentType != null) {
                charset = contentType.charset(UTF8);
            }
            return buffer.clone.readString(charset);
        } catch (IOException e) {
            return "";
        }
    }
}
```

### Copyright and license

Copyright © 2018 Wen Hao

Licensed under [Apache License]

[logo]: ./docs/images/logo.png
[HttpStatusRestTemplateHealthCheck.java]: ./src/main/java/com/github/wenhao/resttemplate/health/HttpStatusRestTemplateHealthCheck.java
[HttpStatusOkHttpClientHealthCheck.java]: ./src/main/java/com/github/wenhao/okhttp/health/HttpStatusOkHttpClientHealthCheck.java
[Apache License]: ./LICENSE
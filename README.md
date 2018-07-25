# parrot

Cacheable http server gateway

### Features

* RestTemplate Cache.

### Gradle

```groovy
repositories {
    jcenter()
}

dependencies {
    compile 'com.github.wenhao:parrot:2.1.3'
}
```

### Maven

```xml
<dependency>
    <groupId>com.github.wenhao</groupId>
    <artifactId>parrot</artifactId>
    <version>2.1.3</version>
</dependency>
```

### Build

```
./gradlew clean build
```

### Get Started

#### application.yml

Enabled parrot tools and set included headers.

```yaml
parrot:
  resttemplate:
    enabled: true
  okhttp:
    enabled: true
  key: PARROT-CACHE
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
            return buffer.readString(charset);
        } catch (IOException e) {
            return "";
        }
    }
}
```

### Copyright and license

Copyright © 2018 Wen Hao

Licensed under [Apache License]

[HttpStatusRestTemplateHealthCheck.java]: ./src/main/java/com/github/wenhao/resttemplate/health/HttpStatusRestTemplateHealthCheck.java
[HttpStatusOkHttpClientHealthCheck.java]: ./src/main/java/com/github/wenhao/okhttp/health/HttpStatusOkHttpClientHealthCheck.java
[Apache License]: ./LICENSE
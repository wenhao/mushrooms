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
    compile 'com.github.wenhao:parrot:2.0.6'
}
```

### Maven

```xml
<dependency>
    <groupId>com.github.wenhao</groupId>
    <artifactId>parrot</artifactId>
    <version>2.0.6</version>
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
  enabled: true
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

#### Custom Health Check

As default, [HttpStatusHealthCheck.java] added, customize health checks:

```java
@Component
public class CustomHealthCheck implements HealthCheck {

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

### Copyright and license

Copyright © 2018 Wen Hao

Licensed under [Apache License]

[HttpStatusHealthCheck.java]: ./src/main/java/com/github/wenhao/health/HttpStatusHealthCheck.java
[Apache License]: ./LICENSE
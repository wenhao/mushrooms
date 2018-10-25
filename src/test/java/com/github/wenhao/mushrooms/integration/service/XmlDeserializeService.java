package com.github.wenhao.mushrooms.integration.service;

import com.jayway.jsonpath.Configuration;
import com.jayway.jsonpath.JsonPath;
import com.jayway.jsonpath.Option;
import com.jayway.jsonpath.TypeRef;
import com.jayway.jsonpath.spi.json.JacksonJsonProvider;
import com.jayway.jsonpath.spi.mapper.JacksonMappingProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.json.XML;
import org.springframework.stereotype.Service;

import java.util.EnumSet;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class XmlDeserializeService {

    public <T> T get(String body, String root, Class<T> type) {
        final String jsonBody = XML.toJSONObject(body).toString();
        return JsonPath.using(getDefaultConfiguration()).parse(jsonBody).read(root, type);
    }

    public <T> List<T> getObjects(String body, String root, TypeRef<List<T>> typeRef) {
        String jsonBody = XML.toJSONObject(body).toString();
        try {
            return JsonPath.using(getDefaultConfiguration()).parse(jsonBody).read(root, typeRef);
        } catch (Exception e) {
            Configuration configuration = Configuration.builder()
                    .jsonProvider(new JacksonJsonProvider())
                    .mappingProvider(new JacksonMappingProvider())
                    .options(Option.ALWAYS_RETURN_LIST)
                    .build();
            return JsonPath.using(configuration).parse(jsonBody).read(root, typeRef);
        }
    }

    private Configuration getDefaultConfiguration() {
        return Configuration.builder()
                .jsonProvider(new JacksonJsonProvider())
                .mappingProvider(new JacksonMappingProvider())
                .options(EnumSet.noneOf(Option.class))
                .build();
    }

}

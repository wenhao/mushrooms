package com.github.wenhao.integration.service;

import com.jayway.jsonpath.Configuration;
import com.jayway.jsonpath.JsonPath;
import com.jayway.jsonpath.Option;
import com.jayway.jsonpath.TypeRef;
import com.jayway.jsonpath.spi.json.JacksonJsonProvider;
import com.jayway.jsonpath.spi.json.JsonProvider;
import com.jayway.jsonpath.spi.mapper.JacksonMappingProvider;
import com.jayway.jsonpath.spi.mapper.MappingProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.json.XML;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;

@Slf4j
@Service
@RequiredArgsConstructor
public class XmlDeserializeService {


    @PostConstruct
    public void init() {
        Configuration.setDefaults(new Configuration.Defaults() {

            private final JsonProvider jsonProvider = new JacksonJsonProvider();
            private final MappingProvider mappingProvider = new JacksonMappingProvider();

            @Override
            public JsonProvider jsonProvider() {
                return jsonProvider;
            }

            @Override
            public MappingProvider mappingProvider() {
                return mappingProvider;
            }

            @Override
            public Set<Option> options() {
                return EnumSet.noneOf(Option.class);
            }
        });
    }

    public <T> T get(String body, String root, Class<T> type) {
        final String jsonBody = XML.toJSONObject(body).toString();
        return JsonPath.using(Configuration.defaultConfiguration()).parse(jsonBody).read(root, type);
    }

    public <T> List<T> getObjects(String body, String root, TypeRef<List<T>> typeRef) {
        final String jsonBody = XML.toJSONObject(body).toString();
        return JsonPath.using(Configuration.defaultConfiguration()).parse(jsonBody).read(root, typeRef);
    }

}

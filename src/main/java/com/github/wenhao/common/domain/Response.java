package com.github.wenhao.common.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import okhttp3.MediaType;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toList;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Response {

    public static final MediaType APPLICATION_JSON_UTF8 = MediaType.parse("application/json;charset=UTF-8");
    public static final MediaType TEXT_XML_UTF8 = MediaType.parse("text/xml;charset=utf-8");
    private String body;
    @Builder.Default
    private List<Header> headers = new ArrayList<>();
    @Builder.Default
    private MediaType contentType = APPLICATION_JSON_UTF8;

    public static Response empty() {
        return Response.builder()
                .body("")
                .headers(Stream.of(Header.builder()
                        .name("Content-Type")
                        .values(Stream.of(APPLICATION_JSON_UTF8.toString()).collect(toList()))
                        .build())
                        .collect(toList()))
                .build();
    }
}

package com.github.wenhao.mushrooms.common.domain;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Request implements Serializable {

    private String path;
    private String method;
    private List<Parameter> parameters;
    private String body;
    private List<Header> headers;
    private String contentType;
}

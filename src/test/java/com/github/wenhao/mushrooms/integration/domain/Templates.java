package com.github.wenhao.mushrooms.integration.domain;


import java.util.AbstractMap;
import java.util.Map;

public class Templates {
    public static final Map.Entry<String, String> GET_BOOK_PRICE = new AbstractMap.SimpleEntry<>("get_book.ftl", "$.soap:Envelope.soap:Body.m:GetBookResponse");
    public static final Map.Entry<String, String> FAULT = new AbstractMap.SimpleEntry<>("", "$.S:Envelope.S:Body.S:Fault");
}

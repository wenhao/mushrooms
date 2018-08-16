package com.github.wenhao.integration.domain;

import org.apache.commons.collections.keyvalue.DefaultMapEntry;

import java.util.Map;

public class Templates {
    public static final Map.Entry<String, String> GET_BOOK_PRICE = new DefaultMapEntry("get_book.ftl", "$.soap:Envelope.soap:Body.m:GetBookResponse");
    public static final Map.Entry<String, String> FAULT = new DefaultMapEntry("", "$.S:Envelope.S:Body.S:Fault");
}

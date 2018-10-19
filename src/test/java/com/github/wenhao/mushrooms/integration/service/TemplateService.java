package com.github.wenhao.mushrooms.integration.service;

import freemarker.template.Configuration;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Map;

import static org.springframework.ui.freemarker.FreeMarkerTemplateUtils.processTemplateIntoString;

@Slf4j
@Service
@RequiredArgsConstructor
public class TemplateService {

    private final Configuration freemarkerConfiguration;

    public String get(String template, Map<String, Object> values) {
        try {
            return processTemplateIntoString(freemarkerConfiguration.getTemplate(template), values);
        } catch (Exception e) {
            log.error("get templates error", e);
            return "";
        }
    }
}

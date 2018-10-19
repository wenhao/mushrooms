package com.github.wenhao.mushrooms.stub.matcher;

import com.github.wenhao.mushrooms.common.domain.Request;
import lombok.extern.slf4j.Slf4j;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathFactory;
import java.io.StringReader;

import static org.apache.commons.lang.StringUtils.substringAfter;

@Slf4j
public class XpathBodyMatcher implements RequestBodyMatcher {

    @Override
    public boolean isApplicable(final Request stubRequest, final Request realRequest) {
        return realRequest.getContentType().contains("xml") && stubRequest.getBody().startsWith("xpath:");
    }

    @Override
    public boolean match(final Request stubRequest, final Request realRequest) {
        final String xpath = substringAfter(stubRequest.getBody(), "xpath:");
        String requestBody = realRequest.getBody().replaceAll("<\\w*:", "<").replaceAll("</\\w*:", "</");
        try {
            final XPathExpression xPathExpression = XPathFactory.newInstance().newXPath().compile(xpath);
            DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
            Document document = documentBuilder.parse(new InputSource(new StringReader(requestBody)));
            return (Boolean) xPathExpression.evaluate(document, XPathConstants.BOOLEAN);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return false;
        }
    }

}

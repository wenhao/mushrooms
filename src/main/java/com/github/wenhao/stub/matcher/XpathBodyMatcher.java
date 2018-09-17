package com.github.wenhao.stub.matcher;

import com.github.wenhao.common.domain.Request;
import org.apache.commons.lang.StringUtils;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathFactory;
import java.io.StringReader;

public class XpathBodyMatcher implements RequestBodyMatcher {

    @Override
    public boolean isApplicable(final Request request) {
        return request.getContentType().contains("xml") && request.getBody().startsWith("xpath:");
    }

    @Override
    public boolean match(final Request stubRequest, final Request realRequest) {
        final String xpath = StringUtils.substringAfter(stubRequest.getBody(), "xpath:");
        try {
            final XPathExpression xPathExpression = XPathFactory.newInstance().newXPath().compile(xpath);
            DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
            Document document = documentBuilder.parse(new InputSource(new StringReader(realRequest.getBody())));
            return (Boolean) xPathExpression.evaluate(document, XPathConstants.BOOLEAN);
        } catch (Exception e) {
            return false;
        }
    }

}

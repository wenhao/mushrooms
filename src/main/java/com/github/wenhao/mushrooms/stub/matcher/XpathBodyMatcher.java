/*
 * Copyright © 2019, Wen Hao <wenhao@126.com>.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package com.github.wenhao.mushrooms.stub.matcher;

import com.github.wenhao.mushrooms.stub.domain.Request;
import static org.apache.commons.lang3.StringUtils.substringAfter;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathFactory;
import java.io.StringReader;
import java.util.logging.Level;
import java.util.logging.Logger;

public class XpathBodyMatcher implements RequestBodyMatcher {

    private final Logger logger = Logger.getLogger(XpathBodyMatcher.class.getName());

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
            logger.log(Level.INFO, e.getMessage(), e);
            return false;
        }
    }

}

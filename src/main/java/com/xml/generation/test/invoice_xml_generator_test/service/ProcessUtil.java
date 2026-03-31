package com.xml.generation.test.invoice_xml_generator_test.service;

import org.w3c.dom.Document;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;

public interface ProcessUtil {
    default String getNodeContentXpth(XPath xpath, Document doc, String tagPath) throws XPathExpressionException {
        XPathExpression expr = xpath.compile(tagPath);
        return (String) expr.evaluate(doc, XPathConstants.STRING);
    }

}

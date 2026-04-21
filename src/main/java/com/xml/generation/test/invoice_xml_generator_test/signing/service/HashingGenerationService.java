package com.xml.generation.test.invoice_xml_generator_test.signing.service;

public interface HashingGenerationService {
    String getInvoiceHash(String xmlDocument);
}

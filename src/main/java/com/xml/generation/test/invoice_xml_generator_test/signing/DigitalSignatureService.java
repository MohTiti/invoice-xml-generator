package com.xml.generation.test.invoice_xml_generator_test.signing;

import com.xml.generation.test.invoice_xml_generator_test.signing.model.DigitalSignature;

import java.security.PrivateKey;

public interface DigitalSignatureService {
    DigitalSignature getDigitalSignature(String xmlDocument, PrivateKey privateKey, String xmlHashing);
}

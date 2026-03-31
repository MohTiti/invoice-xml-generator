package com.xml.generation.test.invoice_xml_generator_test.signing.model;

public class DigitalSignature {
    private String digitalSignature;
    private byte[] xmlHashing;

    public String getDigitalSignature() {
        return digitalSignature;
    }

    public void setDigitalSignature(String digitalSignature) {
        this.digitalSignature = digitalSignature;
    }

    public byte[] getXmlHashing() {
        return xmlHashing;
    }

    public void setXmlHashing(byte[] xmlHashing) {
        this.xmlHashing = xmlHashing;
    }
}

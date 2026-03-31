package com.xml.generation.test.invoice_xml_generator_test.model.enums;


public enum CertificateStatus {
    PENDING("PENDING"),
    PROCESSING("PROCESSING"),
    ACTIVE("ACTIVE"),
    FAILED("FAILED"),
    EXPIRED("EXPIRED"),
    CANCELLED("CANCELLED"),
    REVOKED("REVOKED");

    private final String value;

    CertificateStatus(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}

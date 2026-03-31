package com.xml.generation.test.invoice_xml_generator_test.model.enums;

public enum AdditionalBuyerIdType {
    NATIONAL_ID_NUMBER("NIN"), PERSONAL_NUMBER("PN"), TAXPAYER_NUMBERS("TN"), SERIAL_INCOME_NUMBER("SIN");
    private String value;

    AdditionalBuyerIdType(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}

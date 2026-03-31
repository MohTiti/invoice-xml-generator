package com.xml.generation.test.invoice_xml_generator_test.model.enums;

import java.math.BigDecimal;

public enum GeneralTaxType {
    ZERO(new BigDecimal(0)), EXEMPT(new BigDecimal(0)), ONE(new BigDecimal(1)),
    TWO(new BigDecimal(2)), THREE(new BigDecimal(3)), THREE_HALF(new BigDecimal("3.5")), FOUR(new BigDecimal(4)),
    FIVE(new BigDecimal(5)), SIX(new BigDecimal(6)), SEVEN(new BigDecimal(7)), EIGHT(new BigDecimal(8)),
    TEN(new BigDecimal(10)), SIXTEEN(new BigDecimal(16));

    private BigDecimal percentage;

    GeneralTaxType(BigDecimal percentage) {
        this.percentage = percentage;
    }

    public BigDecimal getPercentage() {
        return percentage;
    }

    public static GeneralTaxType getGeneralTaxTypeByPercentage(double percentage) {
        for (GeneralTaxType generalTaxType : GeneralTaxType.values()) {
            if(percentage == generalTaxType.getPercentage().doubleValue()) {
                return generalTaxType;
            }
        }
        return null;
    }
}

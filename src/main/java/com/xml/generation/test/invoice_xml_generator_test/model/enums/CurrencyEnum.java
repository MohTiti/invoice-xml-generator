package com.xml.generation.test.invoice_xml_generator_test.model.enums;

public enum CurrencyEnum {
    JOD,USD,EUR,SAR,AED, OMR,GBP,QAR,KWD,BHD,AUD,CAD,JPY,CHF,TRY,SYP,EGP;

    public static CurrencyEnum getByValue(String value) {
        for (CurrencyEnum currencyEnum : CurrencyEnum.values()) {
            if(currencyEnum.toString().equals(value)) {
                return currencyEnum;
            }
        }
        return null;
    }
}
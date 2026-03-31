package com.xml.generation.test.invoice_xml_generator_test.model.enums;

public enum InvoiceTypeEnum {
    CASH_INCOME("011"),CASH_GENERAL_TAX("012"),CASH_SPECIAL_TAX("013"),RECEIVABLE_INCOME("021")
    ,RECEIVABLE_GENERAL_TAX("022"),RECEIVABLE_SPECIAL_TAX("023");
    String typeCode;
    InvoiceTypeEnum(String typeCode){
        this.typeCode = typeCode;
    }

    public String getTypeCode() {
        return typeCode;
    }

    public int getInvoiceTypeAsInt() {
        if(CASH_INCOME.equals(this) || RECEIVABLE_INCOME.equals(this)) {
            return 0;
        } else if(CASH_GENERAL_TAX.equals(this) || RECEIVABLE_GENERAL_TAX.equals(this)) {
            return 1;
        } else if(CASH_SPECIAL_TAX.equals(this) || RECEIVABLE_SPECIAL_TAX.equals(this)) {
            return 2;
        }
        return 0;
    }
}



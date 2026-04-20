package com.xml.generation.test.invoice_xml_generator_test.model.dto;

import com.xml.generation.test.invoice_xml_generator_test.model.enums.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TaxpayerInvoiceDTO {
    private String invoiceUniqueIdentifier;
    private String invoiceNumber;
    private Date invoiceIssueDate;
    private String activityNumber;
    private NoteType noteType;
    private InvoiceKind invoiceKind;
    private CurrencyEnum currency;
    private InvoiceTypeEnum invoiceType;
    private BigDecimal totalDiscountAmount;
    private BigDecimal totalPayableAmount;
    private BigDecimal totalGeneralTaxesAmount;
    private String userName;
    private String buyerName;
    private AdditionalBuyerIdType additionalBuyerIdType;
    private String additionalBuyerId;

}

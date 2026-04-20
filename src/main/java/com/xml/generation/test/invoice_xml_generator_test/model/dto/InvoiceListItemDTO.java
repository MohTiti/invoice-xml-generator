package com.xml.generation.test.invoice_xml_generator_test.model.dto;

import com.xml.generation.test.invoice_xml_generator_test.model.enums.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class InvoiceListItemDTO {
    private String invoiceUniqueIdentifier;
    private String invoiceNumber;
    private InvoiceTypeEnum invoiceTypeCode;
    private String userName;
    private String activity;
    private NoteType noteType;
    @JsonFormat(pattern = "dd-MM-yyyy")
    private LocalDate issueDate;
    private String buyerName;
    private BigDecimal totalPayableAmount;
    private InvoiceStatusEnum invoiceStatusEnum;
    private InvoiceKind invoiceKind;
    private CurrencyEnum currencyEnum;
    private String originalInvoiceNumber;
}

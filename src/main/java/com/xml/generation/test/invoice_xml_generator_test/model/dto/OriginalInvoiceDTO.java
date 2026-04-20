package com.xml.generation.test.invoice_xml_generator_test.model.dto;

import com.xml.generation.test.invoice_xml_generator_test.model.enums.InvoiceTypeEnum;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OriginalInvoiceDTO {
    private String invoiceUniqueIdentifier;
    private String invoiceNumber;
    private BigDecimal totalAmount;
    private InvoiceTypeEnum invoiceType;
    @JsonFormat(pattern = "dd-MM-yyyy")
    private LocalDate issueDate;
}

package com.xml.generation.test.invoice_xml_generator_test.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class InvoiceTotalDTO {
    private List<InvoiceListItemDTO> invoiceList = new ArrayList<>();
    private Integer generatedThisYear;
    private Integer generatedInvoices;
    private Integer generatedNotes;
    private Integer currentPage;
    private Long totalItems;
    private Integer totalPages;
    private BigDecimal totalInvoicePayableAmount;

}

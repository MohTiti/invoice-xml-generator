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
public class TaxPayerInvoiceListDTO {
    private List<TaxpayerInvoiceDTO> taxpayerInvoiceDTOList = new ArrayList<>();
    private BigDecimal totalTaxes;
    private Long totalInvoices;
    private String taxNumber;
    private String taxPayerName;
    private Integer pageSize;
    private Integer currentPage;
    private Long totalItems;
    private Integer totalPages;
}

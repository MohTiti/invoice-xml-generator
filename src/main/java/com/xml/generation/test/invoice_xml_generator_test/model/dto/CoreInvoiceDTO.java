package com.xml.generation.test.invoice_xml_generator_test.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CoreInvoiceDTO {
    private InvoiceDTO invoiceDTO;
    private String submittedXML;
    private String qrCode;
}

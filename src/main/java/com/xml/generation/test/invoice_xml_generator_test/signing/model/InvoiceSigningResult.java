package com.xml.generation.test.invoice_xml_generator_test.signing.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class InvoiceSigningResult {
    private String singedXML;
    private String invoiceHash;
}

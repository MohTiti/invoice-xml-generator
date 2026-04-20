package com.xml.generation.test.invoice_xml_generator_test.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
@Data
@NoArgsConstructor
@AllArgsConstructor
public class InvoiceInfoDTO implements Serializable {
    private String invoiceNumber;
    private String invoiceUniqueIdentifier;
    private String qrCode;
    private byte[] xmlFile;
}

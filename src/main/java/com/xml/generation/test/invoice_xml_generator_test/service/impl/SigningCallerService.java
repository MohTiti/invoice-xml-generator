package com.xml.generation.test.invoice_xml_generator_test.service.impl;

import com.xml.generation.test.invoice_xml_generator_test.model.entity.Invoice;
import com.xml.generation.test.invoice_xml_generator_test.signing.SigningServiceImpl;
import com.xml.generation.test.invoice_xml_generator_test.signing.model.InvoiceSigningResult;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
public class SigningCallerService {

    private final SigningServiceImpl signingService;

    public String sign(String canonicalXml, String qrCode, Invoice invoice) throws Exception {
        InvoiceSigningResult result = signingService.signDocument(canonicalXml, qrCode,invoice);
        return result.getSingedXML();
    }
}

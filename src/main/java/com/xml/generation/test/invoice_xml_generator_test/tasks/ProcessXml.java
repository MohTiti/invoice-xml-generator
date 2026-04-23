package com.xml.generation.test.invoice_xml_generator_test.tasks;

import com.xml.generation.test.invoice_xml_generator_test.logging.CustomLogging;
import com.xml.generation.test.invoice_xml_generator_test.model.data.Invoice;
import com.xml.generation.test.invoice_xml_generator_test.service.impl.XmlRegenerationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;

@Component
@RequiredArgsConstructor
public class ProcessXml {

    private final XmlRegenerationService xmlRegenerationService;

    public String process(Invoice invoice) {
        String signedXml = "";
        try {
            signedXml = generateXml(invoice);
        } catch (Exception e) {
            CustomLogging.logError("INVOICE_PROCESSING_FAILED", invoice.getInvoiceId(),
                    "Processing failed for invoiceId={}, skipping: {}", invoice.getInvoiceId(), e.getMessage());
        }
        return signedXml;
    }

    private String generateXml(Invoice invoice) throws Exception {
        String taxNumber     = invoice.getUser().getTaxpayer().getTaxNumber();
        String invoiceNumber = invoice.getInvoiceNumber();
        Long   invoiceId     = invoice.getInvoiceId();

        CustomLogging.logInfo(taxNumber, invoiceNumber, invoiceId,
                "Generating XML for invoiceId={}", invoiceId);
        long start = System.currentTimeMillis();

        byte[] xmlBytes = xmlRegenerationService.regenerateXml(invoice);
        String xmlString = new String(xmlBytes, StandardCharsets.UTF_8);

        long elapsed = System.currentTimeMillis() - start;
        CustomLogging.logInfo(taxNumber, invoiceNumber, invoiceId,
                "XML generated for invoiceId={}, took={}ms", invoiceId, elapsed);
        return xmlString;
    }
}

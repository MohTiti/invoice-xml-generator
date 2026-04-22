package com.xml.generation.test.invoice_xml_generator_test.tasks;

import com.xml.generation.test.invoice_xml_generator_test.logging.CustomLogging;
import com.xml.generation.test.invoice_xml_generator_test.model.data.Invoice;
import com.xml.generation.test.invoice_xml_generator_test.model.record.XmlGenerationResult;
import com.xml.generation.test.invoice_xml_generator_test.repository.InvoiceRepository;
import com.xml.generation.test.invoice_xml_generator_test.service.impl.XmlRegenerationService;
import lombok.RequiredArgsConstructor;
import org.apache.commons.codec.binary.Base64;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

@Component
@RequiredArgsConstructor
public class ProcessXml {

    private final InvoiceRepository invoiceRepository;
    private final XmlRegenerationService xmlRegenerationService;
    @Value("${xml-gen.invoice-id}")
    private Long testInvoiceId;

    public void processInvoice() {
        Invoice invoice = null;
        try {
            invoice = fetchInvoice();
        } catch (Exception e) {
            CustomLogging.logError("INVOICE_FETCH_FAILED", testInvoiceId,
                    "Failed to fetch invoiceId={}: {}", testInvoiceId, e.getMessage());
            return;
        }

        try {
            XmlGenerationResult xml = generateXml(invoice);
            saveToFile(invoice.getInvoiceNumber(), invoice.getInvoiceId(), xml.xmlString());
        } catch (Exception e) {
            CustomLogging.logError("INVOICE_PROCESSING_FAILED", invoice.getInvoiceId(),
                    "Processing failed for invoiceId={}, skipping: {}", invoice.getInvoiceId(), e.getMessage());
        }
    }

    public XmlGenerationResult processInvoiceForPublisher(Invoice invoice) throws Exception {
        return generateXml(invoice);
    }

    private Invoice fetchInvoice() {
        CustomLogging.logInfo(null, null, testInvoiceId, "Fetching invoiceId={}", testInvoiceId);
        long start = System.currentTimeMillis();

        Invoice invoice = invoiceRepository.findById(testInvoiceId)
                .orElseThrow(() -> {
                    CustomLogging.logError("INVOICE_NOT_FOUND", testInvoiceId,
                            "Invoice not found for id={}", testInvoiceId);
                    return new RuntimeException("Invoice not found: " + testInvoiceId);
                });

        long elapsed = System.currentTimeMillis() - start;
        CustomLogging.logInfo(invoice.getUser().getTaxpayer().getTaxNumber(),
                invoice.getInvoiceNumber(), invoice.getInvoiceId(),
                "Fetched invoiceId={}, items={}, took={}ms",
                invoice.getInvoiceId(), invoice.getInvoiceItems().size(), elapsed);
        return invoice;
    }

    private XmlGenerationResult generateXml(Invoice invoice) throws Exception {
        String taxNumber = invoice.getUser().getTaxpayer().getTaxNumber();
        String invoiceNumber = invoice.getInvoiceNumber();
        Long invoiceId = invoice.getInvoiceId();

        CustomLogging.logInfo(taxNumber, invoiceNumber, invoiceId,
                "Generating XML for invoiceId={}", invoiceId);
        long start = System.currentTimeMillis();

        byte[] xmlBytes = xmlRegenerationService.regenerateXml(invoice);
        String xmlString = new String(xmlBytes, StandardCharsets.UTF_8);

        long elapsed = System.currentTimeMillis() - start;
        CustomLogging.logInfo(taxNumber, invoiceNumber, invoiceId,
                "XML generated for invoiceId={}, took={}ms", invoiceId, elapsed);
        return new XmlGenerationResult(xmlString, xmlBytes);
    }

    private void saveToFile(String invoiceNumber, Long invoiceId, String xmlString) throws Exception {
        CustomLogging.logInfo(null, invoiceNumber, invoiceId, "Saving file for invoiceId={}", invoiceId);

        String fileName = "output_" + invoiceNumber + ".xml";
        long start = System.currentTimeMillis();

        String encoded = Base64.encodeBase64String(xmlString.getBytes(StandardCharsets.UTF_8));

        Path path = Path.of(fileName);
        Files.writeString(path, encoded);

        long elapsed = System.currentTimeMillis() - start;
        CustomLogging.logInfo(null, invoiceNumber, invoiceId,
                "File saved to {}, took={}ms", path.toAbsolutePath(), elapsed);
    }

}

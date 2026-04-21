package com.xml.generation.test.invoice_xml_generator_test.tasks;

import com.xml.generation.test.invoice_xml_generator_test.logging.CustomLogging;
import com.xml.generation.test.invoice_xml_generator_test.model.data.Invoice;
import com.xml.generation.test.invoice_xml_generator_test.repository.InvoiceRepository;
import com.xml.generation.test.invoice_xml_generator_test.service.impl.XmlRegenerationService;
import lombok.RequiredArgsConstructor;
import org.apache.commons.codec.binary.Base64;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
public class ProcessXml {

    private final InvoiceRepository invoiceRepository;
    private final XmlRegenerationService xmlRegenerationService;
//    @Value("${xml-gen.invoice-id}")
//    private Long testInvoiceId;

    public String singleInvoice(Long invoiceId) {
        Invoice invoice = null;
        String signedXml = "";
        try {
            invoice = fetchInvoice(invoiceId);
        } catch (Exception e) {
            CustomLogging.logError("INVOICE_FETCH_FAILED", invoiceId,
                    "Failed to fetch invoiceId={}: {}", invoiceId, e.getMessage());
            return signedXml;
        }

        try {
            signedXml = generateXml(invoice);
//            saveToFile(invoice.getInvoiceNumber(), invoice.getInvoiceId(), signedXml);
        } catch (Exception e) {
            CustomLogging.logError("INVOICE_PROCESSING_FAILED", invoice.getInvoiceId(),
                    "Processing failed for invoiceId={}, skipping: {}", invoice.getInvoiceId(), e.getMessage());
        }
        return signedXml;
    }

    public void tenRandomInvoice() throws Exception {

        // todo remove when done testing the isSigned functionality
        List<Invoice> invoices = fetchTenRandomInvoices().stream()
                .filter(inv -> inv != null && inv.getUser() != null)
                .toList();
        List<Boolean> signedFlags = invoices.stream()
                .map(inv -> xmlRegenerationService.isSigned(inv.getXmlFile()))
                .toList();
        List<String> xmls = generateXmlForTenInvoices(invoices, signedFlags);
        savetoDesktopFolder(invoices, xmls, true, signedFlags);
    }


    private List<Invoice> fetchTenRandomInvoices() {

        return invoiceRepository.findTen();

    }

    private List<String> generateXmlForTenInvoices(List<Invoice> invoice, List<Boolean> signedFlags) throws Exception {
        List<String> xmlStrings = new ArrayList<>();
        for (Invoice inv : invoice) {
            try {
                xmlStrings.add(inv.getXmlFile() != null
                        ? new String(inv.getXmlFile(), StandardCharsets.UTF_8)
                        : "");
            } catch (Exception e) {
                CustomLogging.logError("XML_READ_FAILED", inv.getInvoiceId(),
                        "Could not read xmlFile for invoiceId={}, skipping original save: {}", inv.getInvoiceId(), e.getMessage());
                xmlStrings.add("");
            }
        }

        savetoDesktopFolder(invoice, xmlStrings, false, signedFlags);

        long start = System.currentTimeMillis();
        List<String> generatedXmlStrings = new ArrayList<>();
        for (int i = 0; i < invoice.size(); i++) {
            Invoice inv     = invoice.get(i);
            boolean signed  = signedFlags.get(i);
            Long    invoiceId = inv.getInvoiceId();

            try {
                String taxNumber     = inv.getUser().getTaxpayer().getTaxNumber();
                String invoiceNumber = inv.getInvoiceNumber();

                if (signed) {
                    CustomLogging.logWarn("ALREADY_SIGNED", taxNumber, invoiceNumber, invoiceId,
                            "Regenerating already-signed invoiceId={}", invoiceId);
                } else {
                    CustomLogging.logInfo(taxNumber, invoiceNumber, invoiceId,
                            "Generating XML for invoiceId={}", invoiceId);
                }

                byte[] xmlBytes = xmlRegenerationService.regenerateXml(inv, signed);
                generatedXmlStrings.add(new String(xmlBytes, StandardCharsets.UTF_8));
            } catch (Exception e) {
                CustomLogging.logError("BATCH_INVOICE_FAILED", invoiceId,
                        "Batch XML generation failed for invoiceId={}, skipping: {}", invoiceId, e.getMessage());
                generatedXmlStrings.add("");
            }
        }

        long elapsed = System.currentTimeMillis() - start;
        CustomLogging.logInfo(null, null, null,
                "Batch XML generation complete, count={}, took={}ms", invoice.size(), elapsed);
        return generatedXmlStrings;
    }


    private Invoice fetchInvoice(Long invoiceId) {
        CustomLogging.logInfo(null, null, invoiceId, "Fetching invoiceId={}", invoiceId);
        long start = System.currentTimeMillis();

        Invoice invoice = invoiceRepository.findByIdNative(invoiceId)
                .orElseThrow(() -> {
                    CustomLogging.logError("INVOICE_NOT_FOUND", invoiceId,
                            "Invoice not found for id={}", invoiceId);
                    return new RuntimeException("Invoice not found: " + invoiceId);
                });

        long elapsed = System.currentTimeMillis() - start;
        CustomLogging.logInfo(invoice.getUser().getTaxpayer().getTaxNumber(),
                invoice.getInvoiceNumber(), invoice.getInvoiceId(),
                "Fetched invoiceId={}, items={}, took={}ms",
                invoice.getInvoiceId(), invoice.getInvoiceItems().size(), elapsed);
        return invoice;
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

    private void saveToFile(String invoiceNumber, Long invoiceId, String xmlString) throws Exception {
        CustomLogging.logInfo(null, invoiceNumber, invoiceId, "Saving file for invoiceId={}", invoiceId);

        String fileName = "output_" + invoiceNumber + ".xml";
        long start = System.currentTimeMillis();

        String encoded = Base64.encodeBase64String(xmlString.getBytes(StandardCharsets.UTF_8));

        Files.writeString(Path.of(fileName), encoded);

        long elapsed = System.currentTimeMillis() - start;
        CustomLogging.logInfo(null, invoiceNumber, invoiceId,
                "File saved to {}, took={}ms", Path.of(fileName).toAbsolutePath(), elapsed);

//        int rowsUpdated = jdbcTemplate.update("""
//                    UPDATE INVOICE
//                    SET    XML_FILE = ?
//                    WHERE  ID = ?
//                    """,
//                ps -> {
//                    ps.setString(1, xmlString);
//                    ps.setLong(2, invoiceId);
//                });
//
//        if (rowsUpdated > 0) {
//            CustomLogging.logInfo(null, invoiceNumber,
//                    "Successfully updated invoiceId={}", invoiceId);
//        } else {
//            CustomLogging.logWarn("NO_ROW_UPDATED", null,
//                    "No row updated for invoiceId={} — row may not exist", invoiceId);
//        }
    }

    private void savetoDesktopFolder(List<Invoice> invoice, List<String> xmlStrings, boolean isGenerated, List<Boolean> signedFlags) {
        String folderPath = "C:\\Users\\USER\\OneDrive - readytech\\Desktop\\I&R\\";
        for (int i = 0; i < invoice.size(); i++) {
            Invoice inv       = invoice.get(i);
            Long    invoiceId = inv.getInvoiceId();
            try {
                boolean signed       = signedFlags.get(i);
                String  signedPostfix = signed ? "[SIGNED]" : "";

                CustomLogging.logInfo(null, inv.getInvoiceNumber(), invoiceId,
                        "Saving {} invoiceId={} to desktop folder",
                        isGenerated ? "generated" : "original", invoiceId);

                String content = xmlStrings.get(i);
                if (content == null || content.isEmpty()) {
                    CustomLogging.logError("SAVE_SKIPPED_EMPTY", invoiceId,
                            "Skipping save for invoiceId={} — XML content is empty", invoiceId);
                    continue;
                }

                String fileName = isGenerated
                        ? inv.getInvoiceNumber() + "_" + invoiceId + "_generated " + signedPostfix + ".txt"
                        : inv.getInvoiceNumber() + "_" + invoiceId + " " + signedPostfix + ".txt";

                Path path = Paths.get(folderPath + fileName);
                String encoded = Base64.encodeBase64String(content.getBytes(StandardCharsets.UTF_8));
                Files.writeString(path, encoded);
            } catch (Exception e) {
                CustomLogging.logError("SAVE_FAILED", invoiceId,
                        "Failed to save invoiceId={} to desktop, skipping: {}", invoiceId, e.getMessage());
            }
        }
    }


}

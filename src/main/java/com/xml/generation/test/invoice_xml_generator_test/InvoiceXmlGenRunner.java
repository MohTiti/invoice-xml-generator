package com.xml.generation.test.invoice_xml_generator_test;

import com.xml.generation.test.invoice_xml_generator_test.model.entity.Invoice;
import com.xml.generation.test.invoice_xml_generator_test.repository.InvoiceRepository;
import com.xml.generation.test.invoice_xml_generator_test.service.impl.XmlRegenerationService;
import lombok.RequiredArgsConstructor;
import org.apache.commons.codec.binary.Base64;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
public class InvoiceXmlGenRunner implements CommandLineRunner {

    private final InvoiceRepository invoiceRepository;
    private final XmlRegenerationService xmlRegenerationService;
    private final JdbcTemplate jdbcTemplate;


    @Value("${xml-gen.invoice-id}")
    private Long testInvoiceId;

    @Override
    public void run(String... args) throws Exception {
        Invoice invoice = fetchInvoice();
        String signedXml = generateXml(invoice);
        saveToFile(invoice.getInvoiceNumber(), invoice.getInvoiceId(), signedXml);


//        List<Invoice> invoices = fetchTenRandomInvoices().stream()
//                .filter(inv -> inv != null && inv.getUser() != null)
//                .toList();
//        List<Boolean> signedFlags = invoices.stream()
//                .map(inv -> xmlRegenerationService.isSigned(inv.getXmlFile()))
//                .toList();
//        List<String> xmls = generateXmlForTenInvoices(invoices, signedFlags);
//        savetoDesktopFolder(invoices, xmls, true, signedFlags);
    }

    private Invoice fetchInvoice() {
        System.out.println("===============");
        System.out.println("Fetching invoice id=" + testInvoiceId);
        long start = System.currentTimeMillis();

        Invoice invoice = invoiceRepository.findById(testInvoiceId)
                .orElseThrow(() -> new RuntimeException("Invoice not found: " + testInvoiceId));

        long elapsed = System.currentTimeMillis() - start;
        System.out.println("    Invoice number : " + invoice.getInvoiceNumber());
        System.out.println("    Items count    : " + invoice.getInvoiceItems().size());
        System.out.printf("    Time taken     : %d ms%n", elapsed);
        return invoice;
    }

    private List<Invoice> fetchTenRandomInvoices() {

        return invoiceRepository.findTenRandom();

    }

    private String generateXml(Invoice invoice) throws Exception {
        System.out.println("===============");
        System.out.println("Generating XML (QR + signing) for invoice: " + invoice.getInvoiceNumber());
        long start = System.currentTimeMillis();

        byte[] xmlBytes = xmlRegenerationService.regenerateXml(invoice);
        String xmlString = new String(xmlBytes, StandardCharsets.UTF_8);

        long elapsed = System.currentTimeMillis() - start;
        System.out.printf("    Time taken     : %d ms%n", elapsed);
        return xmlString;
    }

    private List<String> generateXmlForTenInvoices(List<Invoice> invoice, List<Boolean> signedFlags) throws Exception {
        System.out.println("===============");
        List<String> xmlStrings = invoice.stream()
                .map(inv -> new String(inv.getXmlFile(), StandardCharsets.UTF_8))
                .toList();

        savetoDesktopFolder(invoice, xmlStrings, false, signedFlags);


        long start = System.currentTimeMillis();
        List<String> generatedXmlStrings = new ArrayList<>();
        for (int i = 0; i < invoice.size(); i++) {
            Invoice inv = invoice.get(i);
            boolean signed = signedFlags.get(i);
            System.out.println("Generating XML (QR + signing) for invoice: " + inv.getInvoiceNumber() + (signed ? " [SIGNED]" : ""));
            byte[] xmlBytes = xmlRegenerationService.regenerateXml(inv, signed);
            String xmlString = new String(xmlBytes, StandardCharsets.UTF_8);
            generatedXmlStrings.add(xmlString);

        }


        long elapsed = System.currentTimeMillis() - start;
        System.out.printf("    Time taken     : %d ms%n", elapsed);
        return generatedXmlStrings;
    }

    private void savetoDesktopFolder(List<Invoice> invoice, List<String> xmlStrings, boolean isGenerated, List<Boolean> signedFlags) throws IOException {
        String folderPath = "C:\\Users\\USER\\OneDrive - readytech\\Desktop\\I&R\\";
        for (int i = 0; i < invoice.size(); i++) {
            boolean signed = signedFlags.get(i);
            String signedPostfix = signed ? "[SIGNED]" : "";

            String source = isGenerated
                    ? "Generated" + invoice.get(i).getInvoiceNumber()
                    : "From Invoice " + invoice.get(i).getInvoiceNumber();

            System.out.println("========= saving to folder invoice " + source + " ==========");

            String fileName = isGenerated
                    ? invoice.get(i).getInvoiceNumber() + "_" + invoice.get(i).getInvoiceId() + "_generated"+ " "+ signedPostfix + ".txt"
                    : invoice.get(i).getInvoiceNumber() + "_" + invoice.get(i).getInvoiceId() +" "+signedPostfix+ ".txt";

            Path path = Paths.get(folderPath + fileName);

            String encoded = Base64.encodeBase64String(xmlStrings.get(i).getBytes(StandardCharsets.UTF_8));

            Files.writeString(path, encoded);
        }


    }


    private void saveToFile(String invoiceNumber, Long invoiceId, String xmlString) throws Exception {
        System.out.println("===============");
        System.out.println("Saving thhe file");

        String fileName = "output_" + invoiceNumber + ".xml";
        long start = System.currentTimeMillis();

        String encoded = Base64.encodeBase64String(xmlString.getBytes(StandardCharsets.UTF_8));

        Files.writeString(Path.of(fileName), encoded);

        long elapsed = System.currentTimeMillis() - start;
        System.out.printf("    Time taken     : %d ms%n", elapsed);
        System.out.println("===============");
        System.out.println("   File saved: " + Path.of(fileName).toAbsolutePath());
        System.out.println("===============");

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
}

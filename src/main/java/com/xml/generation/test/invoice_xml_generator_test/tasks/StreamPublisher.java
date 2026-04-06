package com.xml.generation.test.invoice_xml_generator_test.tasks;

import com.xml.generation.test.invoice_xml_generator_test.logging.CustomLogging;
import com.xml.generation.test.invoice_xml_generator_test.model.entity.Invoice;
import com.xml.generation.test.invoice_xml_generator_test.repository.InvoiceRepository;
import com.xml.generation.test.invoice_xml_generator_test.utils.XmlDecoder;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.stream.Stream;

@Component
@RequiredArgsConstructor
 public class StreamPublisher {

    private final InvoiceRepository invoiceRepository;

    @Value("${publisher.path}")
    private String folderPath;

    @Transactional
    public void publishToDesktop() {
        CustomLogging.logInfo(null, null, null, "Starting invoice stream publish to desktop");

        try (Stream<Invoice> stream = invoiceRepository.findInvoicesStream()) {
            stream.forEach(inv -> {
                Long invoiceId = inv.getInvoiceId();
                try {
                    String taxNumber     = inv.getUser().getTaxpayer().getTaxNumber();
                    String invoiceNumber = inv.getInvoiceNumber();

                    CustomLogging.logInfo(taxNumber, invoiceNumber, invoiceId,
                            "Processing invoiceId={}", invoiceId);

                    LocalDate date  = inv.getIssueDate().toLocalDate();

                    String issueYear  = String.valueOf(date.getYear());
                    String issueMonth = date.format(DateTimeFormatter.ofPattern("MMM", Locale.ENGLISH));
                    String issueDay   = String.format("%02d", date.getDayOfMonth());

                    Path directory = Paths.get(folderPath, taxNumber, issueYear, issueMonth, issueDay);
                    Files.createDirectories(directory);

                    String encodedXml = XmlDecoder.resolveXml(new String(inv.getXmlFile(), StandardCharsets.UTF_8));

                    if (encodedXml == null) {
                        CustomLogging.logError("XML_UNRESOLVABLE", invoiceId,
                                "Skipping invoiceId={} — xml_file could not be decoded", invoiceId);
                        return;
                    }

                    String fileName = String.format("%s_%s.xml",
                            inv.getInvoiceUniqueIdentifier(), invoiceNumber);

                    Path savedPath = directory.resolve(fileName);
                    Files.writeString(savedPath, encodedXml);

                    CustomLogging.logInfo(taxNumber, invoiceNumber, invoiceId,
                            "Saved invoiceId={} to {}", invoiceId, savedPath);

                } catch (Exception e) {
                    CustomLogging.logError("PUBLISH_FAILED", invoiceId,
                            "Failed to publish invoiceId={}: {}", invoiceId, e.getMessage());
                }
            });
        }

        CustomLogging.logInfo(null, null, null, "Invoice stream publish completed");
    }
}

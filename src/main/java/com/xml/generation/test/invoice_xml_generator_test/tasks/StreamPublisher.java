package com.xml.generation.test.invoice_xml_generator_test.tasks;

import com.xml.generation.test.invoice_xml_generator_test.logging.CustomLogging;
import com.xml.generation.test.invoice_xml_generator_test.minio.serivce.MinioStorageService;
import com.xml.generation.test.invoice_xml_generator_test.model.data.Invoice;
import com.xml.generation.test.invoice_xml_generator_test.repository.InvoiceRepository;
import com.xml.generation.test.invoice_xml_generator_test.utils.XmlDecoder;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Stream;

@Component
@RequiredArgsConstructor
@ConditionalOnProperty(name = "program.task-type", havingValue = "PUBLISH")
public class StreamPublisher {

    private final InvoiceRepository invoiceRepository;
    private final MinioStorageService minioStorageService;

    @Value("${publisher.taxpayer}")
    private String taxPayer;

    @Transactional
    public void publishInvoiceXml() {
        AtomicInteger invoiceCount = new AtomicInteger();
        CustomLogging.logInfo(null, null, null, "Starting invoice stream publish to desktop");

        try (Stream<Invoice> stream = invoiceRepository.findInvoicesStream(taxPayer)) {
            stream.forEach(inv -> {
                invoiceCount.getAndIncrement();
                Long invoiceId = inv.getInvoiceId();
                try {
                    String taxNumber = inv.getUser().getTaxpayer().getTaxNumber();
                    String invoiceNumber = inv.getInvoiceNumber();

                    CustomLogging.logInfo(taxNumber, invoiceNumber, invoiceId,
                            "Publishing invoiceId={}", invoiceId);

                    LocalDate date = inv.getIssueDate().toLocalDate();

                    String issueYear = String.valueOf(date.getYear());
                    String issueMonth = date.format(DateTimeFormatter.ofPattern("MMM", Locale.ENGLISH));
                    String issueDay = String.format("%02d", date.getDayOfMonth());

                    String encodedXml = "";

                    //TODO add the xml generation
                    try {
                        encodedXml = XmlDecoder.resolveXml(new String(inv.getXmlFile(), StandardCharsets.UTF_8), inv.getInvoiceId());

                    } catch (Exception e) {
                        CustomLogging.logError("XML_UNRESOLVABLE", invoiceId,
                                "Skipping invoiceId={} — xml_file could not be decoded", invoiceId);
                    }

                    String objectKey = String.format("%s/%s/%s/%s/%s_%s.xml",
                            taxNumber, issueYear, issueMonth, issueDay,
                            inv.getInvoiceUniqueIdentifier(), invoiceNumber);

                    minioStorageService.uploadObject(
                            objectKey,
                            inv.getInvoiceId(),
                            inv.getInvoiceNumber(),
                            encodedXml.getBytes(StandardCharsets.UTF_8),
                            "application/xml"
                    );


                    CustomLogging.logInfo(taxNumber, invoiceNumber, invoiceId,
                            "Saved invoiceId={} to {}", invoiceId, objectKey);

                } catch (Exception e) {
                    CustomLogging.logError("PUBLISH_FAILED", invoiceId,
                            "Failed to publish invoiceId={}: {}", invoiceId, e.getMessage());
                }
            });
        }

        CustomLogging.logInfo(taxPayer, null, null, "Invoice stream publish completed {}", invoiceCount);
    }
}

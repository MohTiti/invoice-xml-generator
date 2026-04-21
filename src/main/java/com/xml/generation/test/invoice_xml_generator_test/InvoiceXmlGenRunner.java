package com.xml.generation.test.invoice_xml_generator_test;

import com.xml.generation.test.invoice_xml_generator_test.logging.CustomLogging;
import com.xml.generation.test.invoice_xml_generator_test.tasks.ProcessXml;
import com.xml.generation.test.invoice_xml_generator_test.tasks.StreamPublisher;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class InvoiceXmlGenRunner implements CommandLineRunner {

    private final ProcessXml processXml;
    private final StreamPublisher streamPublisher;

    @Value("${program.task-type}")
    private String taskType;

    @Override
    public void run(String... args) {
        CustomLogging.logInfo(null, null, null, "InvoiceXmlGenRunner started");
        if ("GENERATE".equals(taskType)) {
            processXml.processInvoice();
        } else if ("PUBLISH".equals(taskType)) {
            streamPublisher.publishInvoiceXml();
        }
        CustomLogging.logInfo(null, null, null, "InvoiceXmlGenRunner finished");
    }
}

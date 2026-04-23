package com.xml.generation.test.invoice_xml_generator_test;

import com.xml.generation.test.invoice_xml_generator_test.logging.CustomLogging;
import com.xml.generation.test.invoice_xml_generator_test.tasks.ProcessXml;
import com.xml.generation.test.invoice_xml_generator_test.tasks.StreamPublisher;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
@ConditionalOnProperty(name = "runner.enabled", havingValue = "true", matchIfMissing = true)
public class InvoiceXmlGenRunner implements CommandLineRunner {

    private final ProcessXml processXml;
    private final Optional<StreamPublisher> streamPublisher;

    @Value("${program.task-type}")
    private String taskType;



    @Override
    public void run(String... args) {
        CustomLogging.logInfo(null, null, null, "InvoiceXmlGenRunner started");
        if ("GENERATE".equals(taskType)) {
            String xmlString = processXml.singleInvoice(1833495884L);
            System.out.println("-------------------------------------------------------------------");
            System.out.println("-------------------------------------------------------------------");
            System.out.println("-------------------------------------------------------------------");
            System.out.println("-------------------------------------------------------------------");
            System.out.println(xmlString);
            System.out.println("-------------------------------------------------------------------");
            System.out.println("-------------------------------------------------------------------");
            System.out.println("-------------------------------------------------------------------");
            System.out.println("-------------------------------------------------------------------");
        } else if ("PUBLISH".equals(taskType)) {
            streamPublisher.ifPresent(StreamPublisher::publishInvoiceXml);
        }
        CustomLogging.logInfo(null, null, null, "InvoiceXmlGenRunner finished");
    }
}

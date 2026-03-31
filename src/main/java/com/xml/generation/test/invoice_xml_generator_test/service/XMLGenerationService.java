package com.xml.generation.test.invoice_xml_generator_test.service;


import com.xml.generation.test.invoice_xml_generator_test.model.dto.InvoiceDTO;
import com.xml.generation.test.invoice_xml_generator_test.model.dto.LuInvoiceTypeDTO;
import freemarker.template.TemplateException;

import java.io.IOException;

public interface XMLGenerationService {

    String generateXML(InvoiceDTO invoiceDTO, String requestFrom, LuInvoiceTypeDTO luInvoiceTypeDTO) throws IOException, TemplateException;

}

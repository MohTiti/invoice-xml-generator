package com.xml.generation.test.invoice_xml_generator_test.model.dto.response;

import com.xml.generation.test.invoice_xml_generator_test.model.enums.InvoiceStatus;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class EInvoicingResult {

    @JsonProperty("EINV_STATUS")
    private InvoiceStatus invoiceStatus;
    @JsonProperty("EINV_SINGED_INVOICE")
    private String submittedInvoice;
    @JsonProperty("EINV_IS_SINGED")
    private boolean isSigned;
    @JsonProperty("EINV_QR")
    private String qrCode;
    @JsonProperty("EINV_NUM")
    private String invoiceNumber;
    @JsonProperty("EINV_INV_UUID")
    private String invoiceUUID;

}


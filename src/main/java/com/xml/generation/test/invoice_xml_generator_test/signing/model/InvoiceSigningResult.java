package com.xml.generation.test.invoice_xml_generator_test.signing.model;

public class InvoiceSigningResult {
    private String singedXML;
    private String invoiceHash;

    public String getSingedXML() {
        return singedXML;
    }

    public void setSingedXML(String singedXML) {
        this.singedXML = singedXML;
    }

    public String getInvoiceHash() {
        return invoiceHash;
    }

    public void setInvoiceHash(String invoiceHash) {
        this.invoiceHash = invoiceHash;
    }
}

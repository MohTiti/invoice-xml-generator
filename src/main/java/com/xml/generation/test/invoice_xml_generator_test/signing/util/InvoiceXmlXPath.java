package com.xml.generation.test.invoice_xml_generator_test.signing.util;

public interface InvoiceXmlXPath {
    String SIGNED_PROPERTIES = "/Invoice/ext:UBLExtensions/ext:UBLExtension/ext:ExtensionContent/sig:UBLDocumentSignatures/sac:SignatureInformation/ds:Signature/ds:Object/xades:QualifyingProperties/xades:SignedProperties";
    String SIGNED_PROPERTIES_SIGNING_TIME =  SIGNED_PROPERTIES + "/xades:SignedSignatureProperties/xades:SigningTime";
    String SIGNED_PROPERTIES_PUBLIC_KEY_HASHING = SIGNED_PROPERTIES + "/xades:SignedSignatureProperties/xades:SigningCertificate/xades:Cert/xades:CertDigest/ds:DigestValue";
    String SIGNED_PROPERTIES_X509_ISSUER_NAME = SIGNED_PROPERTIES + "/xades:SignedSignatureProperties/xades:SigningCertificate/xades:Cert/xades:IssuerSerial/ds:X509IssuerName";
    String SIGNED_PROPERTIES_X509_SERIAL_NUMBER = SIGNED_PROPERTIES + "/xades:SignedSignatureProperties/xades:SigningCertificate/xades:Cert/xades:IssuerSerial/ds:X509SerialNumber";

    String UBL_EXTENSIONS_SIGNATURE = "/Invoice/ext:UBLExtensions/ext:UBLExtension/ext:ExtensionContent/sig:UBLDocumentSignatures/sac:SignatureInformation/ds:Signature";
    String UBL_EXTENSIONS_SIGNATURE_XML_HASHING = UBL_EXTENSIONS_SIGNATURE + "/ds:SignedInfo/ds:Reference[@Id='invoiceSignedData']/ds:DigestValue";
    String UBL_EXTENSIONS_DIGITAL_SIGNATURE = UBL_EXTENSIONS_SIGNATURE + "/ds:SignatureValue";
    String UBL_EXTENSIONS_SIGNATURE_CERTIFICATE = UBL_EXTENSIONS_SIGNATURE + "/ds:KeyInfo/ds:X509Data/ds:X509Certificate";
    String UBL_EXTENSIONS_SIGNED_PROPERTIES_HASHING = UBL_EXTENSIONS_SIGNATURE + "/ds:SignedInfo/ds:Reference[@URI='#xadesSignedProperties']/ds:DigestValue";

    String INVOICE_QR_CODE = "/Invoice/cac:AdditionalDocumentReference[cbc:ID='QR']/cac:Attachment/cbc:EmbeddedDocumentBinaryObject";
}

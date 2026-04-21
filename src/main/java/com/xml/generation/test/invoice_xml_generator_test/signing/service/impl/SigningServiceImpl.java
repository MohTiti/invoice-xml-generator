package com.xml.generation.test.invoice_xml_generator_test.signing.service.impl;

import com.xml.generation.test.invoice_xml_generator_test.model.data.Invoice;
import com.xml.generation.test.invoice_xml_generator_test.signing.model.DigitalSignature;
import com.xml.generation.test.invoice_xml_generator_test.signing.model.InvoiceSigningResult;
import com.xml.generation.test.invoice_xml_generator_test.signing.service.DigitalSignatureService;
import com.xml.generation.test.invoice_xml_generator_test.signing.service.HashingGenerationService;
import com.xml.generation.test.invoice_xml_generator_test.signing.util.InvoiceXmlXPath;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import lombok.Getter;
import lombok.Setter;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.Node;
import org.dom4j.XPath;
import org.dom4j.DocumentHelper;
import org.dom4j.io.SAXReader;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.xml.sax.SAXException;

import javax.xml.transform.OutputKeys;
import javax.xml.transform.Templates;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.Security;
import java.security.cert.X509Certificate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.xml.generation.test.invoice_xml_generator_test.logging.CustomLogging;
import java.util.stream.Collectors;

public class SigningServiceImpl {
    private static final DateTimeFormatter DATE_TIME_FORMATTER =
            DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss'Z'");

    private static final DigitalSignatureService DIGITAL_SIGNATURE_SERVICE = new DigitalSignatureServiceImpl();
    private static final HashingGenerationService HASHING_GENERATION_SERVICE = new HashingGenerationServiceImpl();

    private Templates removeElementsTemplates;
    private Templates addUBLElementTemplates;
    private Templates addQRElementTemplates;
    private Templates addSignatureElementTemplates;

    private String ublElement;
    private String qrElement;
    private String signatureElement;

    private final Map<String, String> nameSpacesMap;

    @Value("${signing.include-comment:false}")
    private boolean includeComment;

    @Setter
    @Getter
    private PrivateKey privateKey;

    @Setter
    @Getter
    private X509Certificate certificate;

    @Setter
    @Getter
    private String certificateAsString;

    private final ThreadLocal<SAXReader> xmlReaderThreadLocal = new ThreadLocal<>() {
        @Override
        protected SAXReader initialValue() {
            try {
                SAXReader reader = new SAXReader();
                reader.setFeature("http://apache.org/xml/features/disallow-doctype-decl", true);
                reader.setFeature("http://xml.org/sax/features/external-general-entities", false);
                reader.setFeature("http://xml.org/sax/features/external-parameter-entities", false);
                reader.setFeature("http://xml.org/sax/features/namespaces", true);
                return reader;
            } catch (SAXException e) {
                CustomLogging.logWarn("SAXREADER_INIT_WARN", null, null, null,
                        "SAXReader feature init warning, using default reader: {}", e.getMessage());
                return new SAXReader();
            }
        }
    };

    private final ThreadLocal<MessageDigest> digestThreadLocal = new ThreadLocal<>() {
        @Override
        protected MessageDigest initialValue() {
            try {
                return MessageDigest.getInstance("SHA-256");
            } catch (NoSuchAlgorithmException e) {
                CustomLogging.logError("DIGEST_INIT_FAILED", null,
                        "SHA-256 MessageDigest unavailable — signing will fail: {}", e.getMessage());
                return null;
            }
        }
    };

    public SigningServiceImpl() {
        nameSpacesMap = new HashMap<>();
        nameSpacesMap.put("cac", "urn:oasis:names:specification:ubl:schema:xsd:CommonAggregateComponents-2");
        nameSpacesMap.put("cbc", "urn:oasis:names:specification:ubl:schema:xsd:CommonBasicComponents-2");
        nameSpacesMap.put("ext", "urn:oasis:names:specification:ubl:schema:xsd:CommonExtensionComponents-2");
        nameSpacesMap.put("sig", "urn:oasis:names:specification:ubl:schema:xsd:CommonSignatureComponents-2");
        nameSpacesMap.put("sac", "urn:oasis:names:specification:ubl:schema:xsd:SignatureAggregateComponents-2");
        nameSpacesMap.put("sbc", "urn:oasis:names:specification:ubl:schema:xsd:SignatureBasicComponents-2");
        nameSpacesMap.put("ds",  "http://www.w3.org/2000/09/xmldsig#");
        nameSpacesMap.put("xades", "http://uri.etsi.org/01903/v1.3.2#");

        Security.addProvider(new BouncyCastleProvider());
    }

    @PostConstruct
    public void init() {
        TransformerFactory transformerFactory = TransformerFactory.newInstance();

        try {
            removeElementsTemplates = transformerFactory.newTemplates(
                    new StreamSource(new ClassPathResource("xslt/removeElements.xsl").getInputStream()));
        } catch (Exception e) {
            CustomLogging.logError("XSLT_LOAD_FAILED", null,
                    "Could not load removeElements.xsl — signing will fail: {}", e.getMessage());
        }

        try {
            addUBLElementTemplates = transformerFactory.newTemplates(
                    new StreamSource(new ClassPathResource("xslt/addUBLElement.xsl").getInputStream()));
        } catch (Exception e) {
            CustomLogging.logError("XSLT_LOAD_FAILED", null,
                    "Could not load addUBLElement.xsl — signing will fail: {}", e.getMessage());
        }

        try {
            addQRElementTemplates = transformerFactory.newTemplates(
                    new StreamSource(new ClassPathResource("xslt/addQRElement.xsl").getInputStream()));
        } catch (Exception e) {
            CustomLogging.logError("XSLT_LOAD_FAILED", null,
                    "Could not load addQRElement.xsl — signing will fail: {}", e.getMessage());
        }

        try {
            addSignatureElementTemplates = transformerFactory.newTemplates(
                    new StreamSource(new ClassPathResource("xslt/addSignatureElement.xsl").getInputStream()));
        } catch (Exception e) {
            CustomLogging.logError("XSLT_LOAD_FAILED", null,
                    "Could not load addSignatureElement.xsl — signing will fail: {}", e.getMessage());
        }

        try {
            ublElement = new BufferedReader(
                    new InputStreamReader(new ClassPathResource("xml/ubl.xml").getInputStream(), StandardCharsets.UTF_8))
                    .lines().collect(Collectors.joining("\n"));
            if (!includeComment) {
                ublElement = ublElement.replace("<!-- Please note that the signature values are sample values only -->", "");
            }
        } catch (IOException e) {
            CustomLogging.logError("XML_RESOURCE_LOAD_FAILED", null,
                    "Could not load xml/ubl.xml — signing will fail: {}", e.getMessage());
        }

        try {
            qrElement = new BufferedReader(
                    new InputStreamReader(new ClassPathResource("xml/qr.xml").getInputStream(), StandardCharsets.UTF_8))
                    .lines().collect(Collectors.joining("\n"));
        } catch (IOException e) {
            CustomLogging.logError("XML_RESOURCE_LOAD_FAILED", null,
                    "Could not load xml/qr.xml — signing will fail: {}", e.getMessage());
        }

        try {
            signatureElement = new BufferedReader(
                    new InputStreamReader(new ClassPathResource("xml/signature.xml").getInputStream(), StandardCharsets.UTF_8))
                    .lines().collect(Collectors.joining("\n"));
        } catch (IOException e) {
            CustomLogging.logError("XML_RESOURCE_LOAD_FAILED", null,
                    "Could not load xml/signature.xml — signing will fail: {}", e.getMessage());
        }
    }

    @PreDestroy
    public void release() {
        xmlReaderThreadLocal.remove();
        digestThreadLocal.remove();
    }


    public InvoiceSigningResult signDocument(String xmlDocument, String qrCode, Invoice invoice) throws Exception {
        InvoiceSigningResult result = new InvoiceSigningResult();

        String invoiceHash;
        try {
            invoiceHash = HASHING_GENERATION_SERVICE.getInvoiceHash(xmlDocument);
        } catch (Exception e) {
            CustomLogging.logError("HASH_FAILED", invoice.getId(),
                    "Failed to hash XML for invoiceId={}: {}", invoice.getId(), e.getMessage());
            throw new Exception("Unable to generate hash for invoice XML: " + e.getMessage(), e);
        }
        result.setInvoiceHash(invoiceHash);

        DigitalSignature digitalSignature;
        try {
            digitalSignature = DIGITAL_SIGNATURE_SERVICE.getDigitalSignature(xmlDocument, privateKey, invoiceHash);
        } catch (Exception e) {
            CustomLogging.logError("DIGITAL_SIGNATURE_FAILED", invoice.getId(),
                    "Failed to create digital signature for invoiceId={}: {}", invoice.getId(), e.getMessage());
            throw new Exception("Unable to create digital signature: " + e.getMessage(), e);
        }

        String transformedXml = transformXML(xmlDocument);

        Document document = getXmlDocument(transformedXml);

        String certificateHashing = encodeBase64(
                bytesToHex(hashStringToBytes(certificateAsString.getBytes(StandardCharsets.UTF_8)))
                        .getBytes(StandardCharsets.UTF_8));

        String signingTimestamp = DATE_TIME_FORMATTER.format(invoice.getCreatedDate());

        String signedPropertiesHashing = populateSignedSignatureProperties(
                document,
                certificateHashing,
                signingTimestamp,
                certificate.getIssuerX500Principal().getName(),
                certificate.getSerialNumber().toString());

        populateUBLExtensions(
                document,
                digitalSignature.getDigitalSignature(),
                signedPropertiesHashing,
                encodeBase64(digitalSignature.getXmlHashing()),
                certificateAsString);

        populateQRCode(document, qrCode);

        result.setSingedXML(document.asXML());
        return result;
    }

    private String transformXML(String xmlDocument) throws TransformerException {
        xmlDocument = transformXml(xmlDocument, removeElementsTemplates);
        xmlDocument = transformXml(xmlDocument, addUBLElementTemplates);
        xmlDocument = xmlDocument.replace("UBL-TO-BE-REPLACED", ublElement);
        xmlDocument = transformXml(xmlDocument, addQRElementTemplates);
        xmlDocument = xmlDocument.replace("QR-TO-BE-REPLACED", qrElement);
        xmlDocument = transformXml(xmlDocument, addSignatureElementTemplates);
        xmlDocument = xmlDocument.replace("SIGN-TO-BE-REPLACED", signatureElement);
        return xmlDocument;
    }

    private String transformXml(String xmlDocument, Templates templates) throws TransformerException {
        if (templates == null) {
            throw new TransformerException("XSLT Templates not initialized — check classpath resources under xslt/");
        }
        Transformer transformer = templates.newTransformer();
        transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
        transformer.setOutputProperty(OutputKeys.INDENT, "no");
        transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "no");
        transformer.setOutputProperty(OutputKeys.METHOD, "xml");

        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        transformer.transform(new StreamSource(new StringReader(xmlDocument)), new StreamResult(bos));
        return bos.toString(StandardCharsets.UTF_8);
    }

    private void populateQRCode(Document document, String qrCode) {
        populateXmlElementText(document, InvoiceXmlXPath.INVOICE_QR_CODE, qrCode);
    }

    private void populateUBLExtensions(Document document, String digitalSignature,
                                        String signedPropertiesHashing, String xmlHashing,
                                        String certificatePem) {
        populateXmlElementText(document, InvoiceXmlXPath.UBL_EXTENSIONS_DIGITAL_SIGNATURE, digitalSignature);
        populateXmlElementText(document, InvoiceXmlXPath.UBL_EXTENSIONS_SIGNATURE_CERTIFICATE, certificatePem);
        populateXmlElementText(document, InvoiceXmlXPath.UBL_EXTENSIONS_SIGNED_PROPERTIES_HASHING, signedPropertiesHashing);
        populateXmlElementText(document, InvoiceXmlXPath.UBL_EXTENSIONS_SIGNATURE_XML_HASHING, xmlHashing);
    }

    private String populateSignedSignatureProperties(Document document, String publicKeyHashing,
                                                      String signatureTimestamp, String x509IssuerName,
                                                      String serialNumber) {
        populateXmlElementText(document, InvoiceXmlXPath.SIGNED_PROPERTIES_PUBLIC_KEY_HASHING, publicKeyHashing);
        populateXmlElementText(document, InvoiceXmlXPath.SIGNED_PROPERTIES_SIGNING_TIME, signatureTimestamp);
        populateXmlElementText(document, InvoiceXmlXPath.SIGNED_PROPERTIES_X509_ISSUER_NAME, x509IssuerName);
        populateXmlElementText(document, InvoiceXmlXPath.SIGNED_PROPERTIES_X509_SERIAL_NUMBER, serialNumber);

        String signedPropertiesXml = getNodeXmlValue(document);
        return encodeBase64(
                bytesToHex(hashStringToBytes(signedPropertiesXml.getBytes(StandardCharsets.UTF_8)))
                        .getBytes(StandardCharsets.UTF_8));
    }

    private void populateXmlElementText(Document document, String xpathExpr, String value) {
        XPath xpath = DocumentHelper.createXPath(xpathExpr);
        xpath.setNamespaceURIs(nameSpacesMap);
        List<Node> nodes = xpath.selectNodes(document);
        nodes.stream().map(node -> (Element) node)
                .forEach(el -> el.setText(value != null ? value : ""));
    }

    private String getNodeXmlValue(Document document) {
        XPath xpath = DocumentHelper.createXPath(InvoiceXmlXPath.SIGNED_PROPERTIES);
        xpath.setNamespaceURIs(nameSpacesMap);
        Node node = xpath.selectSingleNode(document);
        return node != null ? node.asXML() : null;
    }

    private Document getXmlDocument(String xmlDocument) throws DocumentException {
        SAXReader reader = xmlReaderThreadLocal.get();
        return reader.read(new ByteArrayInputStream(xmlDocument.getBytes(StandardCharsets.UTF_8)));
    }

    private byte[] hashStringToBytes(byte[] toBeHashed) {
        MessageDigest md = digestThreadLocal.get();
        if (md == null) {
            CustomLogging.logError("DIGEST_NULL", null,
                    "SHA-256 MessageDigest is null — SHA-256 may be unavailable");
            throw new IllegalStateException("SHA-256 MessageDigest failed to initialize");
        }
        md.reset();
        return md.digest(toBeHashed);
    }

    private String encodeBase64(byte[] bytes) {
        return Base64.getEncoder().encodeToString(bytes);
    }

    private String bytesToHex(byte[] hash) {
        StringBuilder hex = new StringBuilder(2 * hash.length);
        for (byte b : hash) {
            String h = Integer.toHexString(0xff & b);
            if (h.length() == 1) hex.append('0');
            hex.append(h);
        }
        return hex.toString();
    }

    private String getCurrentTimestamp() {
        return DATE_TIME_FORMATTER.format(LocalDateTime.now());
    }

}

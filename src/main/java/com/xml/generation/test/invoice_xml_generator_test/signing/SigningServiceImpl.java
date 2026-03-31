package com.xml.generation.test.invoice_xml_generator_test.signing;

import com.xml.generation.test.invoice_xml_generator_test.model.entity.Invoice;
import com.xml.generation.test.invoice_xml_generator_test.signing.model.DigitalSignature;
import com.xml.generation.test.invoice_xml_generator_test.signing.model.InvoiceSigningResult;
import com.xml.generation.test.invoice_xml_generator_test.signing.util.InvoiceXmlXPath;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
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
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * Embedded signing service — mirrors the einvoicing-signing-java SigningServiceImpl exactly,
 * ported to Spring Boot 3.x (jakarta.annotation) and embedded in this project so no
 * external signing service call is required.
 *
 * Pipeline:
 *   1. Hash the canonical XML (SHA-256, base64)
 *   2. ECDSA-sign the hash with the configured private key
 *   3. Transform XML via 4 XSLT passes to inject UBL extension scaffolding, QR placeholder,
 *      and Signature placeholder
 *   4. Populate the injected scaffold with: certificate hash, signing time, issuer info,
 *      digital signature, XML hash, certificate string, and QR code
 *   5. Return the fully signed XML and its hash
 */
public class SigningServiceImpl {

    private static final Logger LOGGER = Logger.getLogger(SigningServiceImpl.class.getName());
    private static final DateTimeFormatter DATE_TIME_FORMATTER =
            DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss'Z'");

    private static final DigitalSignatureService DIGITAL_SIGNATURE_SERVICE = new DigitalSignatureServiceImpl();
    private static final HashingGenerationService HASHING_GENERATION_SERVICE = new HashingGenerationServiceImpl();

    // XSLT Templates — compiled once at startup, thread-safe for reading
    private Templates removeElementsTemplates;
    private Templates addUBLElementTemplates;
    private Templates addQRElementTemplates;
    private Templates addSignatureElementTemplates;

    // XML fragment strings loaded from classpath resources
    private String ublElement;
    private String qrElement;
    private String signatureElement;

    private final Map<String, String> nameSpacesMap;

    @Value("${signing.include-comment:false}")
    private boolean includeComment;

    // Injected by SigningConfig
    private PrivateKey privateKey;
    private X509Certificate certificate;
    private String certificateAsString;

    // Thread-local SAXReader — SAXReader is not thread-safe
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
                LOGGER.warning("SAXReader init warning, using default: " + e.getMessage());
                return new SAXReader();
            }
        }
    };

    // Thread-local MessageDigest — MessageDigest is not thread-safe
    private final ThreadLocal<MessageDigest> digestThreadLocal = new ThreadLocal<>() {
        @Override
        protected MessageDigest initialValue() {
            try {
                return MessageDigest.getInstance("SHA-256");
            } catch (NoSuchAlgorithmException e) {
                LOGGER.severe("SHA-256 not available: " + e.getMessage());
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
            LOGGER.warning("Could not load removeElements.xsl: " + e.getMessage());
        }

        try {
            addUBLElementTemplates = transformerFactory.newTemplates(
                    new StreamSource(new ClassPathResource("xslt/addUBLElement.xsl").getInputStream()));
        } catch (Exception e) {
            LOGGER.warning("Could not load addUBLElement.xsl: " + e.getMessage());
        }

        try {
            addQRElementTemplates = transformerFactory.newTemplates(
                    new StreamSource(new ClassPathResource("xslt/addQRElement.xsl").getInputStream()));
        } catch (Exception e) {
            LOGGER.warning("Could not load addQRElement.xsl: " + e.getMessage());
        }

        try {
            addSignatureElementTemplates = transformerFactory.newTemplates(
                    new StreamSource(new ClassPathResource("xslt/addSignatureElement.xsl").getInputStream()));
        } catch (Exception e) {
            LOGGER.warning("Could not load addSignatureElement.xsl: " + e.getMessage());
        }

        try {
            ublElement = new BufferedReader(
                    new InputStreamReader(new ClassPathResource("xml/ubl.xml").getInputStream(), StandardCharsets.UTF_8))
                    .lines().collect(Collectors.joining("\n"));
            if (!includeComment) {
                ublElement = ublElement.replace("<!-- Please note that the signature values are sample values only -->", "");
            }
        } catch (IOException e) {
            LOGGER.warning("Could not load xml/ubl.xml: " + e.getMessage());
        }

        try {
            qrElement = new BufferedReader(
                    new InputStreamReader(new ClassPathResource("xml/qr.xml").getInputStream(), StandardCharsets.UTF_8))
                    .lines().collect(Collectors.joining("\n"));
        } catch (IOException e) {
            LOGGER.warning("Could not load xml/qr.xml: " + e.getMessage());
        }

        try {
            signatureElement = new BufferedReader(
                    new InputStreamReader(new ClassPathResource("xml/signature.xml").getInputStream(), StandardCharsets.UTF_8))
                    .lines().collect(Collectors.joining("\n"));
        } catch (IOException e) {
            LOGGER.warning("Could not load xml/signature.xml: " + e.getMessage());
        }
    }

    @PreDestroy
    public void release() {
        xmlReaderThreadLocal.remove();
        digestThreadLocal.remove();
    }


    public InvoiceSigningResult signDocument(String xmlDocument, String qrCode, Invoice invoice) throws Exception {
        InvoiceSigningResult result = new InvoiceSigningResult();

        // Step 1: hash the canonical XML
        String invoiceHash;
        try {
            invoiceHash = HASHING_GENERATION_SERVICE.getInvoiceHash(xmlDocument);
        } catch (Exception e) {
            throw new Exception("Unable to generate hash for invoice XML: " + e.getMessage(), e);
        }
        result.setInvoiceHash(invoiceHash);

        // Step 2: ECDSA-sign the hash
        DigitalSignature digitalSignature;
        try {
            digitalSignature = DIGITAL_SIGNATURE_SERVICE.getDigitalSignature(xmlDocument, privateKey, invoiceHash);
        } catch (Exception e) {
            throw new Exception("Unable to create digital signature: " + e.getMessage(), e);
        }

        // Step 3: inject UBL extension, QR, and Signature scaffolding via XSLT
        String transformedXml = transformXML(xmlDocument);

        // Step 4: parse to DOM4J and populate all placeholder values
        Document document = getXmlDocument(transformedXml);

        String certificateHashing = encodeBase64(
                bytesToHex(hashStringToBytes(certificateAsString.getBytes(StandardCharsets.UTF_8)))
                        .getBytes(StandardCharsets.UTF_8));

        String signingTimestamp = DATE_TIME_FORMATTER.format(invoice.getCreatedDate());

        String signedPropertiesHashing = populateSignedSignatureProperties(
                document,
                certificateHashing,
                //todo to edit time of singning
                signingTimestamp,
                certificate.getIssuerDN().getName(),
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

    // -------------------------------------------------------------------------
    // XSLT transformation pipeline
    // -------------------------------------------------------------------------

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

    // -------------------------------------------------------------------------
    // DOM4J population helpers
    // -------------------------------------------------------------------------

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

    /**
     * Populates the xades:SignedProperties fields, then hashes and base64-encodes the
     * resulting XML node — exactly as the signing service does.
     */
    private String populateSignedSignatureProperties(Document document, String publicKeyHashing,
                                                      String signatureTimestamp, String x509IssuerName,
                                                      String serialNumber) {
        populateXmlElementText(document, InvoiceXmlXPath.SIGNED_PROPERTIES_PUBLIC_KEY_HASHING, publicKeyHashing);
        populateXmlElementText(document, InvoiceXmlXPath.SIGNED_PROPERTIES_SIGNING_TIME, signatureTimestamp);
        populateXmlElementText(document, InvoiceXmlXPath.SIGNED_PROPERTIES_X509_ISSUER_NAME, x509IssuerName);
        populateXmlElementText(document, InvoiceXmlXPath.SIGNED_PROPERTIES_X509_SERIAL_NUMBER, serialNumber);

        String signedPropertiesXml = getNodeXmlValue(document, InvoiceXmlXPath.SIGNED_PROPERTIES);
        return encodeBase64(
                bytesToHex(hashStringToBytes(signedPropertiesXml.getBytes(StandardCharsets.UTF_8)))
                        .getBytes(StandardCharsets.UTF_8));
    }

    private void populateXmlElementText(Document document, String xpathExpr, String value) {
        XPath xpath = DocumentHelper.createXPath(xpathExpr);
        xpath.setNamespaceURIs(nameSpacesMap);
        List<Node> nodes = xpath.selectNodes(document);
        IntStream.range(0, nodes.size())
                .mapToObj(i -> (Element) nodes.get(i))
                .forEach(el -> el.setText(value));
    }

    private String getNodeXmlValue(Document document, String xpathExpr) {
        XPath xpath = DocumentHelper.createXPath(xpathExpr);
        xpath.setNamespaceURIs(nameSpacesMap);
        Node node = xpath.selectSingleNode(document);
        return node != null ? node.asXML() : null;
    }

    private Document getXmlDocument(String xmlDocument) throws DocumentException {
        SAXReader reader = xmlReaderThreadLocal.get();
        return reader.read(new ByteArrayInputStream(xmlDocument.getBytes(StandardCharsets.UTF_8)));
    }

    // -------------------------------------------------------------------------
    // Crypto utilities
    // -------------------------------------------------------------------------

    private byte[] hashStringToBytes(byte[] toBeHashed) {
        MessageDigest md = digestThreadLocal.get();
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

    // -------------------------------------------------------------------------
    // Getters / setters (set by SigningConfig)
    // -------------------------------------------------------------------------

    public PrivateKey getPrivateKey() { return privateKey; }
    public void setPrivateKey(PrivateKey privateKey) { this.privateKey = privateKey; }

    public X509Certificate getCertificate() { return certificate; }
    public void setCertificate(X509Certificate certificate) { this.certificate = certificate; }

    public String getCertificateAsString() { return certificateAsString; }
    public void setCertificateAsString(String certificateAsString) { this.certificateAsString = certificateAsString; }
}

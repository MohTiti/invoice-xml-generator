package com.xml.generation.test.invoice_xml_generator_test.service.impl;

import com.payneteasy.tlv.BerTag;
import com.payneteasy.tlv.BerTlvBuilder;
import com.xml.generation.test.invoice_xml_generator_test.service.ProcessUtil;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.security.*;
import java.util.Base64;


@Service
public class QrGeneratorService implements ProcessUtil {

    private static final String SIGN_ALGORITHM = "SHA256withECDSA";

    private static final String SELLER_NAME_XPATH   = "/Invoice/AccountingSupplierParty/Party/PartyLegalEntity/RegistrationName";
    private static final String TAX_NUMBER_XPATH     = "/Invoice/AccountingSupplierParty/Party/PartyTaxScheme/CompanyID";
    private static final String ISSUE_DATE_XPATH     = "/Invoice/IssueDate";
    private static final String INVOICE_TOTAL_XPATH  = "/Invoice/LegalMonetaryTotal/PayableAmount";
    private static final String TAX_TOTAL_XPATH      = "/Invoice/TaxTotal/TaxAmount";
    private static final String INVOICE_NUMBER_XPATH = "/Invoice/ID";

    @Value("${qr.signing.private.key}")
    private String privateKeyEncodedBase64;

    @Value("${qr.generation.service.id}")
    private String qrGenerationServiceId;

    private PrivateKey privateKey;

    @PostConstruct
    public void init() throws IOException {
        this.privateKey = loadPrivateKey(privateKeyEncodedBase64);
    }

    public String generateQRstatically(String invoiceXml)
            throws ParserConfigurationException, SAXException, IOException, XPathExpressionException {

        Document doc   = parseXml(invoiceXml);
        XPath    xpath = XPathFactory.newInstance().newXPath();

        String sellerName    = getNodeContentXpth(xpath, doc, SELLER_NAME_XPATH);
        String taxNumber     = getNodeContentXpth(xpath, doc, TAX_NUMBER_XPATH);
        String invoiceNumber = getNodeContentXpth(xpath, doc, INVOICE_NUMBER_XPATH);
        String issueDate     = getNodeContentXpth(xpath, doc, ISSUE_DATE_XPATH);
        String invoiceTotal  = getNodeContentXpth(xpath, doc, INVOICE_TOTAL_XPATH);
        String taxTotal      = getNodeContentXpth(xpath, doc, TAX_TOTAL_XPATH);

        String valueString  = toValueString(invoiceNumber, sellerName, taxNumber, issueDate, invoiceTotal, taxTotal);
        String hashAsString = hashToBase64(valueString);
        String signature    = generateSignatureValue(privateKey, hashAsString);

        BerTlvBuilder tlv = new BerTlvBuilder();
        tlv.addText(new BerTag(0x01), qrGenerationServiceId, StandardCharsets.UTF_8);
        tlv.addText(new BerTag(0x02), "{}",                  StandardCharsets.UTF_8);
        tlv.addText(new BerTag(0x03), "false",               StandardCharsets.UTF_8);
        tlv.addText(new BerTag(0x04), invoiceTotal,          StandardCharsets.UTF_8);
        tlv.addText(new BerTag(0x05), invoiceNumber,         StandardCharsets.UTF_8);
        tlv.addText(new BerTag(0x06), taxTotal,              StandardCharsets.UTF_8);
        tlv.addText(new BerTag(0x07), issueDate,             StandardCharsets.UTF_8);
        tlv.addText(new BerTag(0x08), taxNumber,             StandardCharsets.UTF_8);
        tlv.addText(new BerTag(0x09), sellerName,            StandardCharsets.UTF_8);
        tlv.addText(new BerTag(0x0A), signature,             StandardCharsets.UTF_8);

        return Base64.getEncoder().encodeToString(tlv.buildArray());
    }

    private PrivateKey loadPrivateKey(String base64Key) throws IOException {
        String pem = "-----BEGIN EC PRIVATE KEY-----\n"
                + new String(Base64.getDecoder().decode(base64Key))
                + "\n-----END EC PRIVATE KEY-----";
        Reader reader = new InputStreamReader(
                new ByteArrayInputStream(pem.getBytes(StandardCharsets.UTF_8)));
        Object parsed = new org.bouncycastle.openssl.PEMParser(reader).readObject();
        KeyPair pair  = new org.bouncycastle.openssl.jcajce.JcaPEMKeyConverter()
                .getKeyPair((org.bouncycastle.openssl.PEMKeyPair) parsed);
        return pair.getPrivate();
    }

    private Document parseXml(String xml) throws ParserConfigurationException, SAXException, IOException {
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        DocumentBuilder db = dbf.newDocumentBuilder();
        return db.parse(new InputSource(new StringReader(xml)));
    }

    private String hashToBase64(String input) throws RuntimeException {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(input.getBytes(StandardCharsets.UTF_8));
            return Base64.getEncoder().encodeToString(hash);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("SHA-256 not available", e);
        }
    }

    private String generateSignatureValue(PrivateKey key, String hash) {
        return Base64.getEncoder().encodeToString(signECDSA(key, hash));
    }

    private byte[] signECDSA(PrivateKey key, String data) {
        try {
            Provider bc = new org.bouncycastle.jce.provider.BouncyCastleProvider();
            Security.addProvider(bc);
            Signature sig = Signature.getInstance(SIGN_ALGORITHM, bc);
            sig.initSign(key);
            sig.update(data.getBytes(StandardCharsets.UTF_8));
            return sig.sign();
        } catch (Exception e) {
            throw new RuntimeException("ECDSA signing failed", e);
        }
    }

    private String toValueString(String invoiceNumber, String sellerName, String taxNumber,
                                  String issueDate, String invoiceTotal, String taxTotal) {
        return invoiceTotal + invoiceNumber + taxTotal + issueDate + taxNumber + sellerName;
    }
}

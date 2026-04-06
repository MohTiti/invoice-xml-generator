package com.xml.generation.test.invoice_xml_generator_test.signing;

import com.xml.generation.test.invoice_xml_generator_test.logging.CustomLogging;
import com.xml.generation.test.invoice_xml_generator_test.signing.model.DigitalSignature;

import java.nio.charset.StandardCharsets;
import java.security.PrivateKey;
import java.security.Signature;
import java.util.Base64;

public class DigitalSignatureServiceImpl implements DigitalSignatureService {

    /**
     * Signs the invoice XML hash with ECDSA using the supplied private key.
     * The xmlHashing parameter is the base64-encoded SHA-256 hash of the XML.
     * The signature is computed over the raw hash bytes (decoded from base64).
     */
    @Override
    public DigitalSignature getDigitalSignature(String xmlDocument, PrivateKey privateKey, String xmlHashing) {
        final byte[] xmlHashingBytes = Base64.getDecoder().decode(xmlHashing.getBytes(StandardCharsets.UTF_8));
        byte[] digitalSignatureBytes = signECDSA(privateKey, xmlHashingBytes);

        DigitalSignature digitalSignature = new DigitalSignature();
        digitalSignature.setDigitalSignature(Base64.getEncoder().encodeToString(digitalSignatureBytes));
        digitalSignature.setXmlHashing(xmlHashingBytes);
        return digitalSignature;
    }

    private byte[] signECDSA(PrivateKey privateKey, byte[] messageHash) {
        try {
            Signature signature = Signature.getInstance("SHA256withECDSA");
            signature.initSign(privateKey);
            signature.update(messageHash);
            return signature.sign();
        } catch (Exception e) {
            CustomLogging.logError("ECDSA_SIGN_FAILED", null,
                    "ECDSA signing failed: {}", e.getMessage());
            throw new RuntimeException("ECDSA signing failed", e);
        }
    }
}

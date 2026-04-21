package com.xml.generation.test.invoice_xml_generator_test.signing.service.impl;

import com.xml.generation.test.invoice_xml_generator_test.logging.CustomLogging;
import com.xml.generation.test.invoice_xml_generator_test.signing.service.HashingGenerationService;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

public class HashingGenerationServiceImpl implements HashingGenerationService {

    private static MessageDigest digest;

    static {
        try {
            digest = MessageDigest.getInstance("SHA-256");
        } catch (NoSuchAlgorithmException e) {
            CustomLogging.logError("DIGEST_INIT_FAILED", null,
                    "SHA-256 algorithm unavailable — invoice hashing will fail: {}", e.getMessage());
        }
    }

    @Override
    public String getInvoiceHash(String xmlDocument) {
        return Base64.getEncoder().encodeToString(hashStringToBytes(xmlDocument.getBytes(StandardCharsets.UTF_8)));
    }

    private byte[] hashStringToBytes(byte[] bytes) {
        if (digest == null) {
            CustomLogging.logError("DIGEST_NULL", null,
                    "SHA-256 MessageDigest is null — cannot hash invoice XML");
            throw new IllegalStateException("SHA-256 MessageDigest failed to initialize");
        }
        synchronized (digest) {
            return digest.digest(bytes);
        }
    }
}

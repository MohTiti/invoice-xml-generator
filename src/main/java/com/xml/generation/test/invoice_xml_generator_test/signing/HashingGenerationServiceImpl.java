package com.xml.generation.test.invoice_xml_generator_test.signing;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.logging.Level;
import java.util.logging.Logger;

public class HashingGenerationServiceImpl implements HashingGenerationService {

    private static final Logger LOG = Logger.getLogger(HashingGenerationServiceImpl.class.getName());

    private static MessageDigest digest;

    static {
        try {
            digest = MessageDigest.getInstance("SHA-256");
        } catch (NoSuchAlgorithmException e) {
            LOG.log(Level.SEVERE, e.getMessage());
        }
    }

    @Override
    public String getInvoiceHash(String xmlDocument) {
        return Base64.getEncoder().encodeToString(hashStringToBytes(xmlDocument.getBytes(StandardCharsets.UTF_8)));
    }

    private byte[] hashStringToBytes(byte[] bytes) {
        synchronized (digest) {
            return digest.digest(bytes);
        }
    }
}

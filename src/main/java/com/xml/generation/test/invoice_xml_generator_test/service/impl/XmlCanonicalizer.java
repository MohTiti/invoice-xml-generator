package com.xml.generation.test.invoice_xml_generator_test.service.impl;

import org.apache.xml.security.Init;
import org.apache.xml.security.c14n.CanonicalizationException;
import org.apache.xml.security.c14n.Canonicalizer;
import org.apache.xml.security.c14n.InvalidCanonicalizerException;
import org.apache.xml.security.parser.XMLParserException;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

@Service
public class XmlCanonicalizer {


    public String minifyAndCanonicalize(String xml)
            throws IOException, CanonicalizationException {
        String minified = minify(xml.getBytes(StandardCharsets.UTF_8));
        return canonicalize(minified.getBytes(StandardCharsets.UTF_8));
    }

    private String minify(byte[] xmlBytes) throws IOException {
        BufferedReader reader = new BufferedReader(
                new InputStreamReader(new ByteArrayInputStream(xmlBytes)));
        StringBuilder sb = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            sb.append(line.trim());
        }
        return sb.toString();
    }

    private String canonicalize(byte[] xmlBytes)
            throws CanonicalizationException, IOException {
        try {
            Init.init();
            Canonicalizer canon = Canonicalizer.getInstance(Canonicalizer.ALGO_ID_C14N11_OMIT_COMMENTS);
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            canon.canonicalize(xmlBytes, out, true);
            return out.toString(StandardCharsets.UTF_8);
        } catch (InvalidCanonicalizerException | XMLParserException e) {
            throw new CanonicalizationException("C14N11 canonicalizer not available: " + e.getMessage());
        }
    }
}

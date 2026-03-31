package com.xml.generation.test.invoice_xml_generator_test.service;

import org.apache.xml.security.Init;
import org.apache.xml.security.c14n.CanonicalizationException;
import org.apache.xml.security.c14n.Canonicalizer;
import org.apache.xml.security.c14n.InvalidCanonicalizerException;
import org.apache.xml.security.parser.XMLParserException;
import org.springframework.stereotype.Service;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

/**
 * Replicates the SME's MinifyAndCanonicalizeXml pipeline step exactly:
 *   1. Minify  — trim whitespace/newlines from each line
 *   2. Canonicalize — C14N 1.1 without comments (ALGO_ID_C14N11_OMIT_COMMENTS)
 *
 * The canonicalized XML is what must be passed to both QR generation
 * and the signing service, so hashes are consistent.
 */
@Service
public class XmlCanonicalizer {


    public String minifyAndCanonicalize(String xml)
            throws IOException, CanonicalizationException, ParserConfigurationException, SAXException {
        String minified    = minify(xml.getBytes(StandardCharsets.UTF_8));
        return canonicalize(minified.getBytes(StandardCharsets.UTF_8));
    }

    // -------------------------------------------------------------------------

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
            throws CanonicalizationException, ParserConfigurationException, IOException, SAXException {
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

package com.xml.generation.test.invoice_xml_generator_test.utils;

import com.xml.generation.test.invoice_xml_generator_test.logging.CustomLogging;

import java.nio.charset.StandardCharsets;

public class XmlDecoder {
    public static String resolveXml(String content, Long invoiceId) {
        if (looksLikeXml(content)) return content;

        // try decode once
        try {
            String once = new String(java.util.Base64.getDecoder().decode(content), StandardCharsets.UTF_8).trim();
            if (looksLikeXml(once)) return once;

            // try decode twice
            try {
                String twice = new String(java.util.Base64.getDecoder().decode(once), StandardCharsets.UTF_8).trim();
                if (looksLikeXml(twice)) return twice;
            } catch (Exception e) {
                CustomLogging.logError(null, invoiceId, "Double base64 decode failed: {}", e.getMessage());
            }

        } catch (Exception e) {
            CustomLogging.logError(null, invoiceId, "Single base64 decode failed: {}", e.getMessage());
        }

        return null;
    }

    public static boolean looksLikeXml(String content) {
        return content != null && content.trim().startsWith("<");
    }
}

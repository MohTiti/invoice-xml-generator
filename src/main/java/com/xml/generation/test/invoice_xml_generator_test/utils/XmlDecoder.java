package com.xml.generation.test.invoice_xml_generator_test.utils;

import com.xml.generation.test.invoice_xml_generator_test.logging.CustomLogging;
import java.nio.charset.StandardCharsets;

public class XmlDecoder {
    public static String resolveXml(String content) {
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
                CustomLogging.logDebug("Double base64 decode failed: {}", e.getMessage());
            }

        } catch (Exception e) {
            CustomLogging.logDebug("Single base64 decode failed: {}", e.getMessage());
        }

        return null;
    }

    private static boolean looksLikeXml(String content) {
        return content.startsWith("<");
    }
}

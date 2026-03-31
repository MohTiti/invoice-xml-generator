package com.xml.generation.test.invoice_xml_generator_test.logging;

import net.logstash.logback.util.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

public class CustomLogging {

    private static final Logger LOGGER = LoggerFactory.getLogger("jsonLogger");

    public static void logDebug(String message) {
        LOGGER.debug(message);
    }

    public static void logInfo(String taxNumber, String invoiceNumber, String message, Object... args) {
        if (!StringUtils.isBlank(taxNumber)) {
            MDC.put("taxNumber", taxNumber);
        }

        if (!StringUtils.isBlank(invoiceNumber)) {
            MDC.put("invoiceNumber", invoiceNumber);
        }
        LOGGER.info(message, args);
        MDC.clear();
    }

    public static void logWarn(String errorCode, String taxNumber, String message, Object... args
    ) {
        if (!StringUtils.isBlank(errorCode)) {
            MDC.put("errorCode", errorCode);
        }
        if (!StringUtils.isBlank(taxNumber)) {
            MDC.put("taxNumber", taxNumber);
        }
        LOGGER.warn(message, args);
        MDC.clear();

    }

    public static void logError(String errorCode, String message, Object... args
    ) {
        if (!StringUtils.isBlank(errorCode)) {
            MDC.put("errorCode", errorCode);
            LOGGER.error(message, args);
            MDC.remove("errorCode");
        } else {
            LOGGER.error(message, args);
        }
    }

}

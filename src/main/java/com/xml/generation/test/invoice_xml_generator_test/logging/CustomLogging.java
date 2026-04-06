package com.xml.generation.test.invoice_xml_generator_test.logging;

import net.logstash.logback.util.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

public class CustomLogging {

    private static final Logger LOGGER = LoggerFactory.getLogger("jsonLogger");

    public static void logDebug(String message, Object... args) {
        LOGGER.debug(message, args);
    }

    public static void logInfo(String taxNumber, String invoiceNumber, Long invoiceId, String message, Object... args) {
        if (!StringUtils.isBlank(taxNumber))   MDC.put("taxNumber",     taxNumber);
        if (!StringUtils.isBlank(invoiceNumber)) MDC.put("invoiceNumber", invoiceNumber);
        if (invoiceId != null)                 MDC.put("invoiceId",     String.valueOf(invoiceId));
        LOGGER.info(message, args);
        MDC.clear();
    }

    public static void logWarn(String errorCode, String taxNumber, String invoiceNumber, Long invoiceId, String message, Object... args) {
        if (!StringUtils.isBlank(errorCode))   MDC.put("errorCode",     errorCode);
        if (!StringUtils.isBlank(taxNumber))   MDC.put("taxNumber",     taxNumber);
        if (!StringUtils.isBlank(invoiceNumber)) MDC.put("invoiceNumber", invoiceNumber);
        if (invoiceId != null)                 MDC.put("invoiceId",     String.valueOf(invoiceId));
        LOGGER.warn(message, args);
        MDC.clear();
    }

    public static void logError(String errorCode, Long invoiceId, String message, Object... args) {
        if (!StringUtils.isBlank(errorCode)) MDC.put("errorCode", errorCode);
        if (invoiceId != null)               MDC.put("invoiceId", String.valueOf(invoiceId));
        LOGGER.error(message, args);
        MDC.clear();
    }

}

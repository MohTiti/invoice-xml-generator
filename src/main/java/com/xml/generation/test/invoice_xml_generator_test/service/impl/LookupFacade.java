package com.xml.generation.test.invoice_xml_generator_test.service.impl;


import com.xml.generation.test.invoice_xml_generator_test.logging.CustomLogging;
import com.xml.generation.test.invoice_xml_generator_test.model.lookup.*;
import com.xml.generation.test.invoice_xml_generator_test.repository.AdditionalBuyerIdTypeRepository;
import com.xml.generation.test.invoice_xml_generator_test.repository.CurrencyRepository;
import com.xml.generation.test.invoice_xml_generator_test.repository.Isic4LookupRepository;
import com.xml.generation.test.invoice_xml_generator_test.repository.LuInvoiceTypeRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class LookupFacade {

    private final LuInvoiceTypeRepository invoiceTypeRepository;
    private final CurrencyRepository currencyRepository;
    private final AdditionalBuyerIdTypeRepository additionalBuyerIdTypeRepository;
    private final Isic4LookupRepository isic4LookupRepository;

    private static final Map<String, Lu_InvoiceType>        invoiceTypeMap     = new HashMap<>();
    private static final Map<String, Currency>              currencyMap        = new HashMap<>();
    private static final Map<String, AdditionalBuyerIdType> additionalBuyerMap = new HashMap<>();
    private static final Map<String, Isic4Lookup>           isic4Map           = new HashMap<>();

    @PostConstruct
    public void loadAllLookups() {
        CustomLogging.logInfo(null, null,null, "Loading all lookup tables into memory...");

        invoiceTypeRepository.findAll()
                .forEach(t -> invoiceTypeMap.put(t.getCode(), t));

        currencyRepository.findAll()
                .forEach(c -> currencyMap.put(c.getCurrencyEnum(), c));

        additionalBuyerIdTypeRepository.findAll()
                .forEach(a -> additionalBuyerMap.put(a.getCode(), a));

        isic4LookupRepository.findAll()
                .forEach(i -> isic4Map.put(i.getCode(), i));

        CustomLogging.logInfo(null, null, null,"Lookups loaded → invoiceTypes={} currencies={} buyerIdTypes={} isic4={}",
                invoiceTypeMap.size(),
                currencyMap.size(),
                additionalBuyerMap.size(),
                isic4Map.size());
    }


    public static Lu_InvoiceType getInvoiceType(String code) {
        Lu_InvoiceType result = invoiceTypeMap.get(code);
        if (result == null) {
            CustomLogging.logError("LOOKUP_NOT_FOUND", null,
                    "InvoiceType not found for code={}", code);
            throw new IllegalArgumentException("InvoiceType not found for code: " + code);
        }
        return result;
    }

    public static Currency getCurrency(String code) {
        Currency result = currencyMap.get(code);
        if (result == null) {
            CustomLogging.logError("LOOKUP_NOT_FOUND", null,
                    "Currency not found for code={}", code);
            throw new IllegalArgumentException("Currency not found for code: " + code);
        }
        return result;
    }

    public static AdditionalBuyerIdType getAdditionalBuyerIdType(String code) {
        AdditionalBuyerIdType result = additionalBuyerMap.get(code);
        if (result == null) {
            CustomLogging.logError("LOOKUP_NOT_FOUND", null,
                    "AdditionalBuyerIdType not found for code={}", code);
            throw new IllegalArgumentException("AdditionalBuyerIdType not found for code: " + code);
        }
        return result;
    }

    public static LuIsic4Dto getIsic4Dto(String code) {
        String isicCode;
        if (!code.startsWith("isic4_")) {
            isicCode = "isic4_" + code;
        } else {
            isicCode = code;
        }

        Isic4Lookup result = isic4Map.get(isicCode);
        if (result == null) {
            CustomLogging.logError("LOOKUP_NOT_FOUND", null,
                    "Isic4 not found for code={}", code);
            throw new IllegalArgumentException("Isic4 not found for code: " + code);
        }
        return new LuIsic4Dto(
                result.getCode(),
                result.getEnabled(),
                result.getDescriptionAr(),
                result.getDescriptionEn()
        );
    }
}

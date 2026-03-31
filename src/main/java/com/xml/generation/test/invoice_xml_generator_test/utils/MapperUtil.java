package com.xml.generation.test.invoice_xml_generator_test.utils;

import java.util.Map;

public class MapperUtil {

    private static final Map<String, String> CODE_TO_IT = Map.of(
            "LOCAL", "IT-001",
            "EXPORT", "IT-002",
            "DEVELOPMENTAL", "IT-003",
            "FLAG_1", "IT-004",
            "FLAG_2", "IT-005",
            "FLAG_3", "IT-006"
    );

    private static final Map<String, String> IT_TO_CODE = Map.of(
            "IT-001", "LOCAL",
            "IT-002", "EXPORT",
            "IT-003", "DEVELOPMENTAL",
            "IT-004", "FLAG_1",
            "IT-005", "FLAG_2",
            "IT-006", "FLAG_3"
    );

    public static String mapCodeToIT(String code) {
        if (code == null) return null;
        return CODE_TO_IT.getOrDefault(code, code);
    }

    public static String mapITToCode(String itCode) {
        if (itCode == null) return null;
        return IT_TO_CODE.getOrDefault(itCode, itCode);
    }
}
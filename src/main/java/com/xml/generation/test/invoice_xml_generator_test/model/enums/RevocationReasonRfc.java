package com.xml.generation.test.invoice_xml_generator_test.model.enums;


import java.util.Arrays;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public enum RevocationReasonRfc {
    UNSPECIFIED(0),
    KEY_COMPROMISE(1),
    CA_COMPROMISE(2),
    AFFILIATION_CHANGED(3),
    SUPERSEDED(4),
    CESSATION_OF_OPERATION(5),
    CERTIFICATE_HOLD(6),
    CERTIFICATE_ACTIVATION(-1);

    private final int value;

    // Static map for fast lookup by value
    private static final Map<Integer, RevocationReasonRfc> VALUE_MAP =
            Arrays.stream(values())
                    .collect(Collectors.toMap(RevocationReasonRfc::getValue, Function.identity()));

    RevocationReasonRfc(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }


    public static RevocationReasonRfc fromValue(int value) {
        return VALUE_MAP.get(value);
    }


    public static RevocationReasonRfc fromValue(int value, RevocationReasonRfc defaultValue) {
        return VALUE_MAP.getOrDefault(value, defaultValue);
    }

    public static boolean isValidValue(int value) {
        return VALUE_MAP.containsKey(value);
    }


    public String getDisplayName() {
        return name().replace("_", " ").toLowerCase();
    }
}

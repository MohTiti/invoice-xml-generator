package com.xml.generation.test.invoice_xml_generator_test.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TokenDataDTO {
    private String token;
    private String taxNumber;
    private String name;
    private String mobileNumber;
    private String postalCode;
    private boolean registeredAsTaxPayer;
    private List<ActivityDTO> activities = new ArrayList<>();
}

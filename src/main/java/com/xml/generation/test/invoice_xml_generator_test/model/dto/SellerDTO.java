package com.xml.generation.test.invoice_xml_generator_test.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SellerDTO {
    private String taxNumber;
    private String name;
    private String postalCode;
    private String mobileNumber;
    private CountryDTO countryDTO;
    private ActivityDTO activityDTO;
}

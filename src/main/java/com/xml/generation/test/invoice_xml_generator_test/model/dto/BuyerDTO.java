package com.xml.generation.test.invoice_xml_generator_test.model.dto;

import com.xml.generation.test.invoice_xml_generator_test.model.enums.AdditionalBuyerIdType;
import com.xml.generation.test.invoice_xml_generator_test.model.lookup.AdditionalBuyerIdTypeLookupDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BuyerDTO {
    private Long id;
    private String buyerName;
    private String postalCode;
    private String phoneNumber;
    private AdditionalBuyerIdType additionalBuyerIdType;
    private String additionalBuyerId;
    private ProvinceDTO provinceDTO;


}

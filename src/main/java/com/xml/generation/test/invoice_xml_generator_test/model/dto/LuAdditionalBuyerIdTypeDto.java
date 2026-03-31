package com.xml.generation.test.invoice_xml_generator_test.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class LuAdditionalBuyerIdTypeDto {
    private String code;

    private String arabicDescription;

    private String englishDescription;

    private Boolean enabled = true;
}

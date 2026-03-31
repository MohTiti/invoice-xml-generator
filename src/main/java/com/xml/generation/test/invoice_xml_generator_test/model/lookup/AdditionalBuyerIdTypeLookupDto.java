package com.xml.generation.test.invoice_xml_generator_test.model.lookup;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AdditionalBuyerIdTypeLookupDto {
    private String code;
    private String arabicDescription;
    private String englishDescription;
    private boolean isSearchable;
}

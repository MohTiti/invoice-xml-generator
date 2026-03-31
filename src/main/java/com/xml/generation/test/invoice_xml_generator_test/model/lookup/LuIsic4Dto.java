package com.xml.generation.test.invoice_xml_generator_test.model.lookup;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LuIsic4Dto {

    private String code;
    private Boolean enabled = true;
    private String arabicDescription;
    private String englishDescription;

}

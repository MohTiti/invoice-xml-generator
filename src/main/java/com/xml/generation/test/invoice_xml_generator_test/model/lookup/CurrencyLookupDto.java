package com.xml.generation.test.invoice_xml_generator_test.model.lookup;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CurrencyLookupDto {
    private String currency;
    private String arabicDescription;
    private String englishDescription;
}
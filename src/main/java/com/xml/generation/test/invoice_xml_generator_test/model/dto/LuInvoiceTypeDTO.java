package com.xml.generation.test.invoice_xml_generator_test.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LuInvoiceTypeDTO {

    private String code;

    private String arabicDescription;

    private String englishDescription;

    private Integer xmlDigitReference;

    private BigDecimal allowedPercentage;

    private Boolean enabled = true;
}

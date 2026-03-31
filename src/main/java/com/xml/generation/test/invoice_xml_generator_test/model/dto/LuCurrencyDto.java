package com.xml.generation.test.invoice_xml_generator_test.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LuCurrencyDto {

    private String currency;
    private BigDecimal rate;
    private LocalDateTime rateDate;
    private Boolean enabled = true;
    private String arabicDescription;
    private String englishDescription;
}

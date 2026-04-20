package com.xml.generation.test.invoice_xml_generator_test.model.dto;

import lombok.*;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class FieldsErrorsDTO implements Serializable {
    private List<ErrorKeyMessage> errorsKeyMessage = new ArrayList<>();
}

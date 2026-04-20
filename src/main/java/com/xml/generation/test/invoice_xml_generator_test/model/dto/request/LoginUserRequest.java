package com.xml.generation.test.invoice_xml_generator_test.model.dto.request;

import lombok.*;

import java.io.Serializable;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class LoginUserRequest implements Serializable {
    private String username;
    private String password;
}

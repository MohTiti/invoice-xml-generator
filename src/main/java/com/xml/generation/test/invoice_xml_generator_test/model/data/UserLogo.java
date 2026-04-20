package com.xml.generation.test.invoice_xml_generator_test.model.data;

import lombok.Data;

import jakarta.persistence.*;

@Data
@Entity
@Table(name = "user_logo")
public class UserLogo {

    @Id
    @GeneratedValue
    private Long id;

    @Column(name = "activity" , unique = true , nullable = false)
    private String activity;


    @Column(name = "file_name", nullable = false)
    private String fileName;


    @Column(name = "image_logo_data", nullable = false)
    private byte[] imageLogoData;

}

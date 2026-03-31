package com.xml.generation.test.invoice_xml_generator_test.model.entity;


import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "province")
@Data
public class Province {
    @Id
    @GeneratedValue
    private Long id;

    @Column(name = "province_code", columnDefinition = "VARCHAR2(5)")
    private String provinceCode;

    @Column(name = "province_name_en", columnDefinition = "VARCHAR2(50)")
    private String provinceNameEn;

    @Column(name = "province_name_ar", columnDefinition = "VARCHAR2(50)")
    private String provinceNameAr;
}
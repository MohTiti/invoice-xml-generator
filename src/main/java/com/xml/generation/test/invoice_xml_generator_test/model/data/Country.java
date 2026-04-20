package com.xml.generation.test.invoice_xml_generator_test.model.data;


import jakarta.persistence.*;

@Entity
@Table(name = "country")
public class Country {
    @Id
    @GeneratedValue
    private Long id;

    @Column(name = "country_name_ar", columnDefinition = "VARCHAR2(50)")
    private String countryNameAr;

    @Column(name = "country_name_en", columnDefinition = "VARCHAR(50)")
    private String countryNameEn;

    @Column(name = "country_code", columnDefinition = "VARCHAR2(50)")
    private String countryCode;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCountryCode() {
        return countryCode;
    }

    public void setCountryCode(String countryCode) {
        this.countryCode = countryCode;
    }

    public String getCountryNameAr() {
        return countryNameAr;
    }

    public void setCountryNameAr(String countryNameAr) {
        this.countryNameAr = countryNameAr;
    }

    public String getCountryNameEn() {
        return countryNameEn;
    }

    public void setCountryNameEn(String countryNameEn) {
        this.countryNameEn = countryNameEn;
    }
}

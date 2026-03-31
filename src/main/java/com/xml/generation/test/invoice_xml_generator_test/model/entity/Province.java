package com.xml.generation.test.invoice_xml_generator_test.model.entity;


import jakarta.persistence.*;

@Entity
@Table(name = "province")
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


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getProvinceCode() {
        return provinceCode;
    }

    public void setProvinceCode(String provinceCode) {
        this.provinceCode = provinceCode;
    }

    public String getProvinceNameEn() {
        return provinceNameEn;
    }

    public void setProvinceNameEn(String provinceNameEn) {
        this.provinceNameEn = provinceNameEn;
    }

    public String getProvinceNameAr() {
        return provinceNameAr;
    }

    public void setProvinceNameAr(String provinceNameAr) {
        this.provinceNameAr = provinceNameAr;
    }
}

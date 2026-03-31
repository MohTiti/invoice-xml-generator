package com.xml.generation.test.invoice_xml_generator_test.model.lookup;


import com.xml.generation.test.invoice_xml_generator_test.utils.MapperUtil;
import jakarta.persistence.*;

import java.math.BigDecimal;

@Entity
@Table(name = "LU_INVOICE_SUB_TYPE")
public class Lu_InvoiceType {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "invoice_type_seq")
    @SequenceGenerator(name = "invoice_type_seq", sequenceName = "SEQ_LU_INVOICE_SUB_TYPE_ID", allocationSize = 1)
    private Long id;

    @Column(name = "CODE", length = 20, nullable = false, unique = true)
    private String code;

    @Column(name = "ARABIC_DESCRIPTION", nullable = false, length = 200)
    private String arabicDescription;

    @Column(name = "ENGLISH_DESCRIPTION", nullable = false, length = 200)
    private String englishDescription;

    @Column(name = "XML_DIGIT_REFERENCE", unique = true)
    private Integer xmlDigitReference;

    @Column(name = "ALLOWED_PERCENTAGE", precision = 5, scale = 2)
    private BigDecimal allowedPercentage;

    @Column(name = "ENABLED", nullable = false)
    private Boolean enabled = true;

    @PrePersist
    @PreUpdate
    private void validateXmlDigitReference() {
        if (xmlDigitReference != null && (xmlDigitReference < 0 || xmlDigitReference > 9)) {
            throw new IllegalArgumentException("XML Digit Reference must be between 0 and 9");
        }
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCode() {
        return MapperUtil.mapCodeToIT(code);
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getArabicDescription() {
        return arabicDescription;
    }

    public void setArabicDescription(String arabicDescription) {
        this.arabicDescription = arabicDescription;
    }

    public String getEnglishDescription() {
        return englishDescription;
    }

    public void setEnglishDescription(String englishDescription) {
        this.englishDescription = englishDescription;
    }

    public Integer getXmlDigitReference() {
        return xmlDigitReference;
    }

    public void setXmlDigitReference(Integer xmlDigitReference) {
        this.xmlDigitReference = xmlDigitReference;
    }

    public BigDecimal getAllowedPercentage() {
        return allowedPercentage;
    }

    public void setAllowedPercentage(BigDecimal allowedPercentage) {
        this.allowedPercentage = allowedPercentage;
    }

    public Boolean getEnabled() {
        return enabled;
    }

    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }
}
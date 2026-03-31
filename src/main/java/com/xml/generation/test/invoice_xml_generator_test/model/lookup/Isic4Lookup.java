package com.xml.generation.test.invoice_xml_generator_test.model.lookup;

import jakarta.persistence.*;

@Entity
@Table(name = "lu_isic4")
public class Isic4Lookup {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "isic4_seq_gen")
    @SequenceGenerator(
            name = "isic4_seq_gen",
            sequenceName = "SEQ_LU_ISIC_4_ID",
            allocationSize = 1
    )
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "code", nullable = false, unique = true, length = 50)
    private String code;

    @Column(name = "description_en", length = 500)
    private String descriptionEn;

    @Column(name = "description_ar", length = 500)
    private String descriptionAr;

    @Column(name = "enabled", nullable = false)
    private Boolean enabled = true; // Changed to Boolean for better Oracle compatibility

    // Constructors
    public Isic4Lookup() {}

    public Isic4Lookup(String code, String descriptionEn, String descriptionAr) {
        this.code = code;
        this.descriptionEn = descriptionEn;
        this.descriptionAr = descriptionAr;
        this.enabled = true;
    }

    public Isic4Lookup(String code, String descriptionEn, String descriptionAr, Boolean enabled) {
        this.code = code;
        this.descriptionEn = descriptionEn;
        this.descriptionAr = descriptionAr;
        this.enabled = enabled != null ? enabled : true;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getDescriptionEn() {
        return descriptionEn;
    }

    public void setDescriptionEn(String descriptionEn) {
        this.descriptionEn = descriptionEn;
    }

    public String getDescriptionAr() {
        return descriptionAr;
    }

    public void setDescriptionAr(String descriptionAr) {
        this.descriptionAr = descriptionAr;
    }

    public Boolean getEnabled() {
        return enabled;
    }

    public void setEnabled(Boolean enabled) {
        this.enabled = enabled != null ? enabled : true;
    }

    // Utility methods for boolean operations
    public boolean isEnabled() {
        return enabled != null && enabled;
    }

    public void setEnabledBoolean(boolean enabled) {
        this.enabled = enabled;
    }

    @Override
    public String toString() {
        return "Code='" + code + '\'' +
                ", arabicDescription='" + descriptionAr + '\'' +
                ", englishDescription='" + descriptionEn + '\'';
    }

    public String toStringEnableDisable() {
        return "Code='" + code + '\'' +
                ", status=" + ((enabled != null && enabled) ? "ENABLED" : "DISABLED");
    }
}
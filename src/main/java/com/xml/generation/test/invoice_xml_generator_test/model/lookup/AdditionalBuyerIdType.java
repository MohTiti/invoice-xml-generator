package com.xml.generation.test.invoice_xml_generator_test.model.lookup;

import jakarta.persistence.*;

import java.io.Serializable;
import java.util.Objects;

/**
 * Entity class for the ADDITIONAL_BUYER_ID_TYPE table.
 * Represents lookup values for buyer identification types.
 */
@Entity
@Table(name = "LU_ADDITIONAL_BUYER_ID_TYPE")
public class AdditionalBuyerIdType implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "additionalBuyerIdTypeSeq")
    @SequenceGenerator(name = "additionalBuyerIdTypeSeq", sequenceName = "SEQ_LU_ADDITIONAL_BUYER_ID_TYPE", allocationSize = 1)
    @Column(name = "ID", nullable = false, precision = 19, scale = 0)
    private Long id;

    @Column(name = "CODE", nullable = false, length = 10, unique = true)
    private String code;

    @Column(name = "ARABIC_DESCRIPTION", length = 255)
    private String arabicDescription;

    @Column(name = "ENGLISH_DESCRIPTION", nullable = false, length = 255)
    private String englishDescription;

    @Column(name = "ENABLED", nullable = false, precision = 1, scale = 0)
    private Boolean enabled = true;

    /**
     * Default constructor
     */
    public AdditionalBuyerIdType() {
    }

    /**
     * Constructor with required fields
     */
    public AdditionalBuyerIdType(String code, String englishDescription) {
        this.code = code;
        this.englishDescription = englishDescription;
        this.enabled = true;
    }

    /**
     * Full constructor
     */
    public AdditionalBuyerIdType(String code, String arabicDescription, String englishDescription, Boolean enabled) {
        this.code = code;
        this.arabicDescription = arabicDescription;
        this.englishDescription = englishDescription;
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

    public Boolean getEnabled() {
        return enabled;
    }

    public void setEnabled(Boolean enabled) {
        this.enabled = enabled != null ? enabled : true;
    }

    // Equals, HashCode, and ToString methods

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AdditionalBuyerIdType that = (AdditionalBuyerIdType) o;
        return Objects.equals(id, that.id) ||
                (code != null && Objects.equals(code, that.code));
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, code);
    }

    @Override
    public String toString() {
        return "AdditionalBuyerIdType{" +
                "id=" + id +
                ", code='" + code + '\'' +
                ", englishDescription='" + englishDescription + '\'' +
                ", enabled=" + enabled +
                '}';
    }
}
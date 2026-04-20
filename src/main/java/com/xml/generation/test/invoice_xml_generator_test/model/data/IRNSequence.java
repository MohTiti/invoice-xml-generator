package com.xml.generation.test.invoice_xml_generator_test.model.data;

import jakarta.persistence.*;

@Entity
@Table(name = "irn_sequence")
public class IRNSequence {

    @Id
    @GeneratedValue
    private Long id;

    @Column(name = "tax_number", nullable = false, unique = true)
    private String taxNumber;

    @Column(name = "sequence", nullable = false)
    private Integer sequence;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getSequence() {
        return sequence;
    }

    public void setSequence(Integer sequence) {
        this.sequence = sequence;
    }

    public String getTaxNumber() {
        return taxNumber;
    }

    public void setTaxNumber(String taxNumber) {
        this.taxNumber = taxNumber;
    }
}

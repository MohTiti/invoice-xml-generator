package com.xml.generation.test.invoice_xml_generator_test.model.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.util.Set;

@Entity
@Table(name = "tax_payer")
@Data
public class Taxpayer {

    @Id
    @GeneratedValue
    private Long id;

    @Column(name = "tax_number", nullable = false)
    private String taxNumber;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "postal_code", length = 10)
    private String postalCode;

    @Column(name = "mobile_number", nullable = false)
    private String mobileNumber;

    @Column(name = "enabled")
    private boolean enabled;

    @Column(name = "otp_count_trial")
    private Long otpCountTrial;

    @OneToMany(mappedBy = "taxpayer")
    private Set<Activity> activities;

    @ManyToOne
    @JoinColumn(name = "country_id")
    private Country country;

}

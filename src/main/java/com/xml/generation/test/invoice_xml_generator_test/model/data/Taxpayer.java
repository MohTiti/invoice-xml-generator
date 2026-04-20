package com.xml.generation.test.invoice_xml_generator_test.model.data;

import jakarta.persistence.*;
import java.util.Set;

@Entity
@Table(name = "tax_payer")
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

    @OneToMany(mappedBy = "taxpayer")
    private Set<Activity> activities;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name="country_id")
    private Country country;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getPostalCode() {
        return postalCode;
    }

    public void setPostalCode(String postalCode) {
        this.postalCode = postalCode;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public Set<Activity> getActivities() {
        return activities;
    }

    public void setActivities(Set<Activity> activities) {
        this.activities = activities;
    }

    public String getTaxNumber() {
        return taxNumber;
    }

    public void setTaxNumber(String taxNumber) {
        this.taxNumber = taxNumber;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMobileNumber() {
        return mobileNumber;
    }

    public void setMobileNumber(String mobileNumber) {
        this.mobileNumber = mobileNumber;
    }

    public Country getCountry() {
        return country;
    }

    public void setCountry(Country country) {
        this.country = country;
    }
}

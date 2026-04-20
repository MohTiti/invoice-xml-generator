package com.xml.generation.test.invoice_xml_generator_test.model.data;

import com.xml.generation.test.invoice_xml_generator_test.model.enums.AdditionalBuyerIdType;

import jakarta.persistence.*;

@Entity
@Table(name = "buyer")
public class Buyer {
    @Id
    @GeneratedValue
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(name = "additional_buyer_id_type")
    private AdditionalBuyerIdType additionalBuyerIdType;
    @Column(name = "additional_buyer_id", nullable = false, columnDefinition = "VARCHAR(200)")
    private String additionalBuyerId;
    @Column(name = "buyer_name", nullable = false, columnDefinition = "VARCHAR2(255)")
    private String buyerName;
    @Column(name = "phone_number", nullable = false, columnDefinition = "VARCHAR(14)")
    private String phoneNumber;
    @Column(name = "postal_code", nullable = false, columnDefinition = "VARCHAR(5)")
    private String postalCode;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "province_id")
    private Province province;

    public Long getBuyerId() {
        return id;
    }

    public void setBuyerId(Long id) {
        this.id= id;
    }

    public String getAdditionalBuyerId() {
        return additionalBuyerId;
    }

    public void setAdditionalBuyerId(String additionalBuyerId) {
        this.additionalBuyerId = additionalBuyerId;
    }

    public String getBuyerName() {
        return buyerName;
    }

    public void setBuyerName(String buyerName) {
        this.buyerName = buyerName;
    }

    public String getPostalCode() {
        return postalCode;
    }

    public void setPostalCode(String postalCode) {
        this.postalCode = postalCode;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public AdditionalBuyerIdType getAdditionalBuyerIdType() {
        return additionalBuyerIdType;
    }

    public void setAdditionalBuyerIdType(AdditionalBuyerIdType additionalBuyerIdType) {
        this.additionalBuyerIdType = additionalBuyerIdType;
    }

    public Province getProvince() {
        return province;
    }

    public void setProvince(Province province) {
        this.province = province;
    }
}

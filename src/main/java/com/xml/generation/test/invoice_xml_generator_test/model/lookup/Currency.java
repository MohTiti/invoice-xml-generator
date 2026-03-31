package com.xml.generation.test.invoice_xml_generator_test.model.lookup;


import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "lu_currency")
public class Currency {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "currency_seq_gen")
    @SequenceGenerator(
            name = "currency_seq_gen",
            sequenceName = "lu_currency_seq",
            allocationSize = 1
    )
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "currency_code", nullable = false, unique = true, length = 10)
    private String currencyEnum;

    @Column(name = "exchange_rate", precision = 18, scale = 6)
    private BigDecimal rate;

    @Column(name = "rate_date", nullable = false)
    private LocalDateTime rateDate;

    @Column(name = "enabled")
    private Boolean enabled = true;

    @Column(name = "arabic_description", length = 100)
    private String arabicDescription;

    @Column(name = "english_description", length = 100)
    private String englishDescription;

    public String getCurrencyEnum() {
        return currencyEnum;
    }

    public void setCurrencyEnum(String currencyEnum) {
        this.currencyEnum = currencyEnum;
    }

    public BigDecimal getRate() {
        return rate;
    }

    public void setRate(BigDecimal rate) {
        this.rate = rate;
    }

    public LocalDateTime getRateDate() {
        return rateDate;
    }

    public void setRateDate(LocalDateTime rateDate) {
        this.rateDate = rateDate;
    }

    public Boolean getEnabled() {
        return enabled;
    }

    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
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

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
}
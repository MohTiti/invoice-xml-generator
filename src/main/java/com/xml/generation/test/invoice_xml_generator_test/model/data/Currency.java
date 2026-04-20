package com.xml.generation.test.invoice_xml_generator_test.model.data;

import com.xml.generation.test.invoice_xml_generator_test.model.enums.CurrencyEnum;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.sql.Date;

@Entity
@Table(name = "Currency")
public class Currency {

    @Id
    @Enumerated(EnumType.STRING)
    @Column(name = "currency", nullable = false, unique = true)
    private CurrencyEnum currencyEnum;

    @Column(name = "rate", precision = 10, scale = 4 ,nullable = false)
    private BigDecimal rate;

    @Column(name = "rate_date", nullable = false)
    private Date rateDate;

    public CurrencyEnum getCurrencyEnum() {
        return currencyEnum;
    }

    public void setCurrencyEnum(CurrencyEnum currencyEnum) {
        this.currencyEnum = currencyEnum;
    }

    public BigDecimal getRate() {
        return rate;
    }

    public void setRate(BigDecimal rate) {
        this.rate = rate;
    }

    public Date getRateDate() {
        return rateDate;
    }

    public void setRateDate(Date rateDate) {
        this.rateDate = rateDate;
    }
}

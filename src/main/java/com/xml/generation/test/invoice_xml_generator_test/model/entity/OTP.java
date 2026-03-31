package com.xml.generation.test.invoice_xml_generator_test.model.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.sql.Timestamp;

@Entity
@Table(name = "otp")
@Data
public class OTP {
    @Id
    @GeneratedValue
    private Long id;

    @Column(name = "otp", nullable = false)
    private String otp;

    @Column(name = "expiry_date", nullable = false)
    private Timestamp expiryDate;

    @Column(name = "used", nullable = false)
    private boolean used;

    @Column(name = "enabled")
    private boolean enabled;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, referencedColumnName = "id")
    private User user;

}

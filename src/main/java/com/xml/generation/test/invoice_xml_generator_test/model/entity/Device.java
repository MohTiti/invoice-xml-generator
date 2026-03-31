package com.xml.generation.test.invoice_xml_generator_test.model.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "device")
@Data
public class Device {

    @Id
    @GeneratedValue
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "client_id")
    private String clientId;

    @Column(name = "secret_key")
    private String secretKey;

    @Column(name = "enabled", nullable = false)
    private boolean enabled;

    @Column(name = "device_name")
    private String deviceName;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "activity_id", referencedColumnName = "id")
    private Activity activity;

    @OneToMany(mappedBy = "device", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Certificate> certificates = new ArrayList<>();

    public boolean hasAnActiveCertificate() {
        for (Certificate certificate : certificates) {
            if (certificate.isActive()) {
                return true;
            }
        }
        return false;
    }

}
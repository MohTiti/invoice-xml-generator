package com.xml.generation.test.invoice_xml_generator_test.model.entity;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "SERVICE_PROVIDERS", indexes = {
        @Index(name = "idx_service_providers_tax_number", columnList = "tax_number")
})
public class ServiceProvider {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "service_provider_seq_gen")
    @SequenceGenerator(
            name = "service_provider_seq_gen",
            sequenceName = "SEQ_SERVICE_PROVIDERS_ID",
            allocationSize = 1
    )
    @Column(name = "id", nullable = false)
    private Integer id; // Changed to Integer to match original int

    @Column(name = "name", length = 200)
    private String name;

    @Column(name = "tax_number", length = 50, unique = true)
    private String taxNumber;

    @Column(name = "activity", length = 500)
    private String activity;

    @Column(name = "enabled", nullable = false)
    private Boolean enabled = false;

    @Column(name = "created_at")
    private LocalDateTime createdAt;


    public ServiceProvider(String name, String taxNumber, String activity) {
        this.name = name;
        this.taxNumber = taxNumber;
        this.activity = activity;
        this.enabled = false; // Default as per original
        this.createdAt = LocalDateTime.now();
    }

    public ServiceProvider(String name, String taxNumber, String activity, Boolean enabled) {
        this.name = name;
        this.taxNumber = taxNumber;
        this.activity = activity;
        this.enabled = enabled != null ? enabled : false;
        this.createdAt = LocalDateTime.now();
    }

    // Utility methods for boolean operations
    public boolean isEnabled() {
        return enabled != null && enabled;
    }

    public void setEnabledBoolean(boolean enabled) {
        this.enabled = enabled;
    }


    public boolean isActive() {
        return isEnabled() && name != null && !name.trim().isEmpty()
                && taxNumber != null && !taxNumber.trim().isEmpty();
    }

    public String getDisplayName() {
        return name != null && !name.trim().isEmpty() ? name : "Unnamed Provider";
    }

    @PrePersist
    protected void onCreate() {
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
        if (enabled == null) {
            enabled = false;
        }
    }

    @PreUpdate
    protected void onUpdate() {
        if (taxNumber != null) {
            taxNumber = taxNumber.replaceAll("\\s+", "").toUpperCase();
        }
    }

    @Override
    public String toString() {
        return "tax number= " + taxNumber +
                ", activity= " + activity +
                ", enabled= " + enabled;
    }

    public String toDetailedString() {
        return "ServiceProvider{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", taxNumber='" + taxNumber + '\'' +
                ", activity='" + activity + '\'' +
                ", enabled=" + enabled +
                ", createdAt=" + createdAt +
                '}';
    }
}
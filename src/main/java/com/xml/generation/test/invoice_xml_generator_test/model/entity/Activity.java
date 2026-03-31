package com.xml.generation.test.invoice_xml_generator_test.model.entity;


import com.xml.generation.test.invoice_xml_generator_test.model.enums.ActivityUsage;
import jakarta.persistence.*;
import lombok.Data;

import java.util.Set;

@Entity
@Table(name = "activity")
@Data
public class Activity {

    @Id
    @GeneratedValue
    private Long id;

    @Column(name = "activity")
    private String activity;

    @Column(name = "activity_usage")
    @Enumerated(EnumType.STRING)
    private ActivityUsage activityUsage;

    @Column(name = "description")
    private String description;

    @Column(name = "invoice_type")
    private int invoiceType;

    @ManyToOne
    @JoinColumn(name = "taxpayer_id", nullable = false)
    private Taxpayer taxpayer;

    @OneToMany(mappedBy = "activity")
    private Set<User> users;

    @OneToOne
    @JoinColumn(name = "service_provider_id")
    private ServiceProvider serviceProvider;

    @OneToMany(mappedBy = "activity")
    private Set<Device> devices;
    @Column(name = "enable_customer_price", nullable = false)
    private int enableCustomerPrice = 0;
    @Column(name = "CAN_SUBMIT_SUMMARY_INVOICES", nullable = false)
    private int enableSummaryInvoice = 0;

    @ManyToOne
    @JoinColumn(name = "connected_to_service_provider")
    private ServiceProvider connectedToServiceProvider;

}

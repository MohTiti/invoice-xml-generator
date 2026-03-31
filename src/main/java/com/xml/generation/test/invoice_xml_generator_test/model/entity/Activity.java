package com.xml.generation.test.invoice_xml_generator_test.model.entity;


import com.xml.generation.test.invoice_xml_generator_test.model.enums.ActivityUsage;
import jakarta.persistence.*;



@Entity
@Table(name = "activity")
public class Activity {

    @Id
    @GeneratedValue
    private Long id;

    @Column(name = "activity")
    private String activity;

    @Column(name = "description")
    private String description;

    @Column(name = "activity_usage")
    @Enumerated(EnumType.STRING)
    private ActivityUsage activityUsage;

    @Column(name = "invoice_type")
    private int invoiceType;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "taxpayer_id", nullable = false)
    private Taxpayer taxpayer;

    @Column(name = "enable_customer_price", nullable = false)
    private int enableCustomerPrice = 0;

    @Column(name = "CAN_SUBMIT_SUMMARY_INVOICES", nullable = false)
    private int enableSummaryInvoice = 0;

    public int getEnableSummaryInvoice() {
        return enableSummaryInvoice;
    }

    public void setEnableSummaryInvoice(int enableSummaryInvoice) {
        this.enableSummaryInvoice = enableSummaryInvoice;
    }

    public int getEnableCustomerPrice() {
        return enableCustomerPrice;
    }

    public void setEnableCustomerPrice(int enableCustomerPrice) {
        this.enableCustomerPrice = enableCustomerPrice;
    }

    public Activity() {

    }

    public Activity(String activity, String description, Taxpayer taxpayer, ActivityUsage activityUsage, int invoiceType) {
        this.activity = activity;
        this.description = description;
        this.taxpayer = taxpayer;
        this.activityUsage = activityUsage;
        this.invoiceType = invoiceType;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getActivity() {
        return activity;
    }

    public void setActivity(String activity) {
        this.activity = activity;
    }

    public Taxpayer getTaxpayer() {
        return taxpayer;
    }

    public void setTaxpayer(Taxpayer taxpayer) {
        this.taxpayer = taxpayer;
    }

    public String getDescription() {
        return (description != null && !description.trim().equals("")) ? description : "*";
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public ActivityUsage getActivityUsage() {
        return activityUsage;
    }

    public void setActivityUsage(ActivityUsage activityUsage) {
        this.activityUsage = activityUsage;
    }

    public int getInvoiceType() {
        return invoiceType;
    }

    public void setInvoiceType(int invoiceType) {
        this.invoiceType = invoiceType;
    }
}

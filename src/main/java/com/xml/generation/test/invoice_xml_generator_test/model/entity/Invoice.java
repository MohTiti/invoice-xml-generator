package com.xml.generation.test.invoice_xml_generator_test.model.entity;

import com.xml.generation.test.invoice_xml_generator_test.model.entity.Activity;
import com.xml.generation.test.invoice_xml_generator_test.model.entity.Buyer;
import com.xml.generation.test.invoice_xml_generator_test.model.entity.InvoiceItem;
import com.xml.generation.test.invoice_xml_generator_test.model.entity.User;
import com.xml.generation.test.invoice_xml_generator_test.model.enums.*;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.sql.Date;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "invoice")
public class Invoice {
    @Id
    @GeneratedValue
    private Long id;

    @Column(name = "invoice_type", nullable = false)
    @Enumerated(EnumType.STRING)
    private InvoiceTypeEnum invoiceTypeCode;

    @Column(name = "note_type", columnDefinition = "VARCHAR(15)")
    @Enumerated(EnumType.STRING)
    private NoteType noteType;

    @Column(name= "INV_STS ", columnDefinition="VARCHAR(20)")
    private String INV_STS ;


    @Column(name = "buyer_invoice_number", columnDefinition = "VARCHAR(255)")
    private String buyerInvoiceNumber;

    @Column(name = "issue_date", nullable = false)
    private Date issueDate;


    @Column(name = "invoice_number", nullable = false, columnDefinition = "VARCHAR(255)")
    private String invoiceNumber;

    @Column(name= "invoice_status",columnDefinition = "VARCHAR(255)")
    @Enumerated(EnumType.STRING)
    private InvoiceStatusEnum invoiceStatus;

    @Column(name = "invoice_unique_identifier",nullable = false,columnDefinition = "VARCHAR(255)")
    private String invoiceUniqueIdentifier;

    @Column(name = "edit_done",nullable = false)
    private Boolean editDone;

    @Column(name= "qr_code", columnDefinition="text")
    private String qrCode;

    @Column(name= "notes", columnDefinition="text")
    private String notes;

    @Column(name= "invoice_kind",columnDefinition = "VARCHAR(255)")
    private String invoiceKind;



    @Column(name= "reason_of_note", columnDefinition="text")
    private String reasonOfNote;

    @Column(name= "total_excluding_taxes", precision=38, scale=9, nullable = false)
    private BigDecimal totalExcludingTaxes;

    @Column(name = "total_discounts_amount", precision = 38, scale = 9, nullable = false)
    private BigDecimal totalDiscountsAmount;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdDate;

    @Column(name = "total_payable_amount", precision = 38, scale = 9, nullable = false)
    private BigDecimal totalPayableAmount;

    @Column(name = "total_refund_amount", precision = 38, scale = 9, nullable = false)
    private BigDecimal totalRefundAmount;

    @Column(name = "total_general_taxes_amount", precision = 38, scale = 9, nullable = false)
    private BigDecimal totalGeneralTaxesAmount;

    @Column(name = "total_special_taxes_amount", precision = 38, scale = 9, nullable = false)
    private BigDecimal totalSpecialTaxesAmount;

    @Column(name = "margin_of_error", precision = 10, scale = 9, nullable = false)
    private BigDecimal marginOfError = BigDecimal.ZERO;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "original_invoice_id", referencedColumnName = "id")
    private Invoice originalInvoice;

    @OneToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "buyer_id")
    private Buyer buyer;

    @OneToOne
    @JoinColumn(name = "activity_id", nullable = true)
    private Activity activity;

    @Column(name = "currency", columnDefinition = "VARCHAR(15)")
    @Enumerated(EnumType.STRING)
    private CurrencyEnum currency;

    @Column(name = "xml_file", columnDefinition = "BLOB")
    private byte[] xmlFile;

    @OneToMany(fetch = FetchType.EAGER,cascade= CascadeType.ALL, mappedBy="invoice")
    private List<InvoiceItem> invoiceItems = new ArrayList<>();

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

    @Column(name = "request_from", columnDefinition = "VARCHAR(255)")
    @Enumerated(EnumType.STRING)
    private RequestFromEnum requestFromEnum;


    @Column(name = "rate", precision = 10, scale = 4 ,nullable = false)
    private BigDecimal rate;

    @Column(name = "rate_date", nullable = false)
    private Date rateDate;

    @Column(name = "is_succeed")
    private Boolean isSucceed;

    @Column(name = "is_signed")
    private Boolean isSigned;

    public Boolean getSucceed() {
        return isSucceed;
    }

    public void setSucceed(Boolean succeed) {
        isSucceed = succeed;
    }

    public RequestFromEnum getRequestFromEnum() { return requestFromEnum; }

    public void setRequestFromEnum(RequestFromEnum requestFromEnum) { this.requestFromEnum = requestFromEnum; }

    public Activity getActivity() {
        return activity;
    }

    public void setActivity(Activity activity) {
        this.activity = activity;
    }


    public Long getInvoiceId() {
        return id;
    }

    public void setInvoiceId(Long id) {
        this.id = id;
    }
    public String getINV_STS () {
        return INV_STS ;
    }

    public void setINV_STS (String INV_STS ) {
        this.INV_STS  =INV_STS ;
    }

    public LocalDateTime getCreatedDate() {return createdDate;}

    public Boolean getSigned() {
        return isSigned;
    }

    public void setSigned(Boolean signed) {
        isSigned = signed;
    }

    public BigDecimal getMarginOfError() {
        return marginOfError;
    }

    public void setMarginOfError(BigDecimal marginOfError) {
        this.marginOfError = marginOfError;
    }

    public void setCreatedDate(LocalDateTime createdDate) { this.createdDate = createdDate;}

    public InvoiceTypeEnum getInvoiceTypeCode() {
        return invoiceTypeCode;
    }

    public void setInvoiceTypeCode(InvoiceTypeEnum invoiceTypeCode) {
        this.invoiceTypeCode = invoiceTypeCode;
    }

    public Date getIssueDate() {
        return issueDate;
    }

    public void setIssueDate(Date issueDate) {
        this.issueDate = issueDate;
    }

    public void setCurrency(CurrencyEnum currency) {
        this.currency = currency;
    }

    public CurrencyEnum getCurrency(){
        return currency;
    }

    public String getInvoiceNumber() {
        return invoiceNumber;
    }

    public void setInvoiceNumber(String invoiceNumber) {
        this.invoiceNumber = invoiceNumber;
    }

    public InvoiceStatusEnum getInvoiceStatus() {
        return invoiceStatus;
    }

    public void setInvoiceStatus(InvoiceStatusEnum invoiceStatus) {
        this.invoiceStatus = invoiceStatus;
    }

    public String getInvoiceUniqueIdentifier() {
        return invoiceUniqueIdentifier;
    }

    public void setInvoiceUniqueIdentifier(String invoiceUniqueIdentifier) {
        this.invoiceUniqueIdentifier = invoiceUniqueIdentifier;
    }

    public String getQrCode() {
        return qrCode;
    }

    public void setQrCode(String qrCode) {
        this.qrCode = qrCode;
    }

    public String getReasonOfNote() {
        return reasonOfNote;
    }

    public void setReasonOfNote(String reasonOfNote) {
        this.reasonOfNote = reasonOfNote;
    }

    public BigDecimal getTotalPayableAmount() {
        return totalPayableAmount;
    }

    public void setTotalPayableAmount(BigDecimal totalPayableAmount) {
        this.totalPayableAmount = totalPayableAmount;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Buyer getBuyer() {
        return buyer;
    }

    public void setBuyer(Buyer buyer) {
        this.buyer = buyer;
    }

    public String getBuyerInvoiceNumber() {
        return buyerInvoiceNumber;
    }

    public void setBuyerInvoiceNumber(String buyerInvoiceNumber) {
        this.buyerInvoiceNumber = buyerInvoiceNumber;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public String getInvoiceKind() { return invoiceKind;}

    public void setInvoiceKind(String invoiceKind) { this.invoiceKind = invoiceKind; }

    public BigDecimal getTotalExcludingTaxes() {
        return totalExcludingTaxes;
    }

    public void setTotalExcludingTaxes(BigDecimal totalExcludingTaxes) {
        this.totalExcludingTaxes = totalExcludingTaxes;
    }

    public BigDecimal getTotalDiscountsAmount() {
        return totalDiscountsAmount;
    }

    public void setTotalDiscountsAmount(BigDecimal totalDiscountsAmount) {
        this.totalDiscountsAmount = totalDiscountsAmount;
    }

    public BigDecimal getTotalGeneralTaxesAmount() {
        return totalGeneralTaxesAmount;
    }

    public void setTotalGeneralTaxesAmount(BigDecimal totalGeneralTaxesAmount) {
        this.totalGeneralTaxesAmount = totalGeneralTaxesAmount;
    }

    public byte[] getXmlFile() {
        return xmlFile;
    }

    public void setXmlFile(byte[] xmlFile) {
        this.xmlFile = xmlFile;
    }

    public List<InvoiceItem> getInvoiceItems() {
        return invoiceItems;
    }

    public void setInvoiceItems(List<InvoiceItem> invoiceItems) {
        this.invoiceItems = invoiceItems;
    }

    public NoteType getNoteType() {
        return noteType;
    }

    public void setNoteType(NoteType noteType) {
        this.noteType = noteType;
    }

    public BigDecimal getTotalSpecialTaxesAmount() {
        return totalSpecialTaxesAmount;
    }

    public void setTotalSpecialTaxesAmount(BigDecimal totalSpecialTaxesAmount) {
        this.totalSpecialTaxesAmount = totalSpecialTaxesAmount;
    }

    public Boolean getEditDone() {
        return editDone;
    }

    public void setEditDone(Boolean editDone) {
        this.editDone = editDone;
    }

    public BigDecimal getTotalRefundAmount() {
        return totalRefundAmount;
    }

    public void setTotalRefundAmount(BigDecimal totalRefundAmount) {
        this.totalRefundAmount = totalRefundAmount;
    }

    public Invoice getOriginalInvoice() {
        return originalInvoice;
    }

    public void setOriginalInvoice(Invoice originalInvoice) {
        this.originalInvoice = originalInvoice;
    }
}

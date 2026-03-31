package com.xml.generation.test.invoice_xml_generator_test.model.entity;


import com.xml.generation.test.invoice_xml_generator_test.model.enums.CertificateStatus;
import com.xml.generation.test.invoice_xml_generator_test.model.enums.RevocationReasonRfc;
import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Entity
@Table(name = "certificate", indexes = {
        @Index(name = "idx_certificate_device_id", columnList = "device_id"),
        @Index(name = "idx_certificate_status", columnList = "status"),
        @Index(name = "idx_certificate_serial_number", columnList = "serial_number"),
        @Index(name = "idx_certificate_thumbprint", columnList = "thumbprint"),
        @Index(name = "idx_certificate_expiry_date", columnList = "expiry_date"),
        @Index(name = "idx_certificate_created_at", columnList = "created_at")
})
public class Certificate {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "cert_seq_gen")
    @SequenceGenerator(name = "cert_seq_gen", sequenceName = "certificate_seq", allocationSize = 1)
    @Column(name = "id", nullable = false)
    private Long id;

    @ManyToOne()
    @JoinColumn(name = "device_id", nullable = false, foreignKey = @ForeignKey(name = "fk_certificate_device"))
    private Device device;

    @Column(name = "request_id", length = 36)
    private String requestId; // UUID format

    @Lob
    @Column(name = "csr", nullable = false, columnDefinition = "CLOB")
    private String csr; // Certificate Signing Request

    @Column(name = "status", nullable = false, length = 20)
    @Enumerated(EnumType.STRING)
    private CertificateStatus status;

    @Lob
    @Column(name = "certificate", columnDefinition = "CLOB")
    private String certificate; // X.509 certificate in PEM format

    @Column(name = "serial_number", length = 50)
    private String serialNumber;

    @Column(name = "thumbprint", length = 64)
    private String thumbprint; // SHA-1 thumbprint

    @Lob
    @Column(name = "distinguished_name", columnDefinition = "CLOB")
    private String distinguishedName;

    @Column(name = "issued_date")
    private LocalDateTime issuedDate;

    @Column(name = "expiry_date")
    private LocalDateTime expiryDate;

    @Column(name = "revocation_reason")
    private Integer revocationReason;

    @Column(name = "revocation_date")
    private LocalDateTime revocationDate;

    @Column(name = "last_checked")
    private LocalDateTime lastChecked;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "subject_id")
    private Long subject;

    @Column(name = "two_month_notification_sent")
    private LocalDateTime twoMonthNotificationSent;

    @Column(name = "one_week_notification_sent")
    private LocalDateTime oneWeekNotificationSent;

    @ManyToOne()
    @JoinColumn(name = "activity_id", nullable = false, foreignKey = @ForeignKey(name = "fk_certificate_activity"))
    private Activity activity;

    // Constructors
    public Certificate() {}

    public Certificate(Device device, String requestId, String csr, Activity activity) {
        this.device = device;
        this.requestId = requestId;
        this.csr = csr;
        this.activity = activity;
        this.status = CertificateStatus.PENDING;
        this.createdAt = LocalDateTime.now();
    }

    // Utility methods for certificate lifecycle management
    public boolean isActive() {
        return status == CertificateStatus.ACTIVE &&
                !isExpired() &&
                !isRevoked();
    }

    public boolean isExpired() {
        return expiryDate != null && LocalDateTime.now().isAfter(expiryDate);
    }

    public boolean isRevoked() {
        return status == CertificateStatus.REVOKED;
    }

    public boolean isPending() {
        return status == CertificateStatus.PENDING;
    }

    /**
     * Returns the warning level based on certificate expiry date
     * @return "HIGH" if expires within 2 weeks, "WARN" if expires within 2 months, "INFO" if more than 2 months
     */
    public String warningLevel() {
        if (this.expiryDate == null || !this.status.equals(CertificateStatus.ACTIVE)) {
            return "INFO";
        }

        LocalDateTime now = LocalDateTime.now();

        if (now.isAfter(this.expiryDate)) {
            return "HIGH";
        }

        long daysUntilExpiry = java.time.Duration.between(now, this.expiryDate).toDays();

        if (daysUntilExpiry <= 14) {
            return "HIGH";
        } else if (daysUntilExpiry <= 60) {
            return "WARN";
        } else {
            return "INFO";
        }
    }

    /**
     * Legacy method for backward compatibility - checks if certificate expires within given days threshold
     * @param daysThreshold number of days to check
     * @return true if certificate expires within the threshold
     */
    public boolean isNearExpiry(int daysThreshold) {
        if (expiryDate == null) return false;
        return LocalDateTime.now().plusDays(daysThreshold).isAfter(expiryDate);
    }

    public long getDaysUntilExpiry() {
        if (expiryDate == null) return -1;
        return java.time.Duration.between(LocalDateTime.now(), expiryDate).toDays();
    }

    // Certificate operation methods
    public void issueCertificate(String certificateContent, String serialNumber,
                                 String thumbprint, String distinguishedName) {
        this.certificate = certificateContent;
        this.serialNumber = serialNumber;
        this.thumbprint = thumbprint;
        this.distinguishedName = distinguishedName;
        this.status = CertificateStatus.ACTIVE;
        this.issuedDate = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    public void revokeCertificate(Integer revocationReason) {
        this.status = CertificateStatus.REVOKED;
        this.revocationReason = revocationReason;
        this.revocationDate = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    public void markExpired() {
        if (this.status == CertificateStatus.ACTIVE) {
            this.status = CertificateStatus.EXPIRED;
            this.updatedAt = LocalDateTime.now();
        }
    }

    // Validation methods
    public boolean hasValidSerialNumber() {
        return this.serialNumber != null && !this.serialNumber.trim().isEmpty();
    }

    public boolean hasValidThumbprint() {
        return thumbprint != null && thumbprint.matches("[A-Fa-f0-9]{40,64}");
    }

    // Display methods
    public String getStatusDisplayName() {
        if (status == null) return "Unknown";
        return status.name().replace("_", " ").toLowerCase();
    }

    public String getFormattedExpiryDate() {
        if (expiryDate == null) return "Not Set";
        return expiryDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
    }

    public String getFormattedIssuedDate() {
        if (issuedDate == null) return "Not Issued";
        return issuedDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
    }
    public boolean isActiveCertificateStatus() {
        if(this.status == CertificateStatus.PENDING ||
                this.status == CertificateStatus.ACTIVE ||
                this.status == CertificateStatus.PROCESSING){
            return true;
        }
        if(this.status == CertificateStatus.REVOKED){
            if(this.revocationReason != null){
                return this.revocationReason == 6;
            }
        }
        return false;
    }

    public boolean isRevokedWithReasonHold() {
        return this.status == CertificateStatus.REVOKED &&
                this.revocationReason != null &&
                this.revocationReason.equals(6);
    }
    public void cancelCertificate() {
        this.status = CertificateStatus.CANCELLED;
        this.updatedAt = LocalDateTime.now();
    }

    // Lifecycle callbacks
    @PrePersist
    protected void onCreate() {
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
        if (status == null) {
            status = CertificateStatus.PENDING;
        }
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();

        // Auto-mark expired certificates
        if (status == CertificateStatus.ACTIVE && isExpired()) {
            status = CertificateStatus.EXPIRED;
        }
    }

    // Standard getters and setters

    public LocalDateTime getTwoMonthNotificationSent() {
        return twoMonthNotificationSent;
    }

    public void setTwoMonthNotificationSent(LocalDateTime twoMonthNotificationSent) {
        this.twoMonthNotificationSent = twoMonthNotificationSent;
    }

    public LocalDateTime getOneWeekNotificationSent() {
        return oneWeekNotificationSent;
    }

    public void setOneWeekNotificationSent(LocalDateTime oneWeekNotificationSent) {
        this.oneWeekNotificationSent = oneWeekNotificationSent;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Device getDevice() {
        return device;
    }

    public void setDevice(Device device) {
        this.device = device;
    }

    public String getRequestId() {
        return requestId;
    }

    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }

    public String getCsr() {
        return csr;
    }

    public void setCsr(String csr) {
        this.csr = csr;
    }

    public CertificateStatus getStatus() {
        return status;
    }

    public void setStatus(CertificateStatus status) {
        this.status = status;
    }

    public String getCertificate() {
        return certificate;
    }

    public void setCertificate(String certificate) {
        this.certificate = certificate;
    }

    public String getSerialNumber() {
        return serialNumber;
    }

    public void setSerialNumber(String serialNumber) {
        this.serialNumber = serialNumber;
    }

    public String getThumbprint() {
        return thumbprint;
    }

    public void setThumbprint(String thumbprint) {
        this.thumbprint = thumbprint;
    }

    public String getDistinguishedName() {
        return distinguishedName;
    }

    public void setDistinguishedName(String distinguishedName) {
        this.distinguishedName = distinguishedName;
    }

    public LocalDateTime getIssuedDate() {
        return issuedDate;
    }

    public void setIssuedDate(LocalDateTime issuedDate) {
        this.issuedDate = issuedDate;
    }

    public LocalDateTime getExpiryDate() {
        return expiryDate;
    }

    public void setExpiryDate(LocalDateTime expiryDate) {
        this.expiryDate = expiryDate;
    }

    public Integer getRevocationReason() {
        return revocationReason;
    }

    public void setRevocationReason(Integer revocationReason) {
        this.revocationReason = revocationReason;
    }

    public LocalDateTime getRevocationDate() {
        return revocationDate;
    }

    public void setRevocationDate(LocalDateTime revocationDate) {
        this.revocationDate = revocationDate;
    }

    public LocalDateTime getLastChecked() {
        return lastChecked;
    }

    public void setLastChecked(LocalDateTime lastChecked) {
        this.lastChecked = lastChecked;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public Long getSubject() {
        return subject;
    }

    public void setSubject(Long subject) {
        this.subject = subject;
    }

    public Activity getActivity() {
        return activity;
    }

    public void setActivity(Activity activity) {
        this.activity = activity;
    }

    @Override
    public String toString() {
        return "Certificate{" +
                "id=" + id +
                ", requestId='" + requestId + '\'' +
                ", status=" + status +
                ", serialNumber='" + serialNumber + '\'' +
                ", issuedDate=" + issuedDate +
                ", expiryDate=" + expiryDate +
                '}';
    }

    public String toStringRenewal() {
        return "Certificate{" +
                "id=" + id +
                ", status=" + status +
                ", serialNumber='" + serialNumber + '\'' +
                ", expiryDate=" + expiryDate +
                '}';
    }

    public String toStringRevocation() {
        return "Certificate{" +
                "id=" + id +
                ", status=" + status +
                ", revocationReason=" + revocationReason +
                ", serialNumber='" + serialNumber + '\'' +
                ", revocationDate=" + revocationDate +
                '}';
    }


    public String toStringForCertificateIssuance() {
        return "Certificate{" +
                "id=" + id +
                ", requestId='" + requestId + '\'' +
                ", status=" + status +
                '}';
    }
    public boolean isRenewalDisallowed() {
        return  this.status != CertificateStatus.REVOKED ||
                this.getRevocationReason() == null ||
                this.getRevocationReason() != RevocationReasonRfc.CERTIFICATE_HOLD.getValue();
    }

}
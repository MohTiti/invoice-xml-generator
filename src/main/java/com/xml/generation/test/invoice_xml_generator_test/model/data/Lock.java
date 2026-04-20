package com.xml.generation.test.invoice_xml_generator_test.model.data;

import jakarta.persistence.*;
import java.sql.Timestamp;

@Entity
@Table(name = "locks")
public class Lock {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    @Column(name = "lock_name", nullable = false)
    private String lockName;

    @Column(name = "instance_id", nullable = false)
    private String instanceId;

    @Column(name = "acquired_at", nullable = false, columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    private Timestamp acquiredAt;

    @Column(name = "taxpayer_number", nullable = false)
    private String taxpayerNumber;

    public String getTaxNumber() {
        return taxpayerNumber;
    }

    public void setTaxNumber(String taxNumber) {
        this.taxpayerNumber = taxNumber;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getLockName() {
        return lockName;
    }

    public void setLockName(String lockName) {
        this.lockName = lockName;
    }

    public String getInstanceId() {
        return instanceId;
    }

    public void setInstanceId(String instanceId) {
        this.instanceId = instanceId;
    }

    public Timestamp getAcquiredAt() {
        return acquiredAt;
    }

    public void setAcquiredAt(Timestamp acquiredAt) {
        this.acquiredAt = acquiredAt;
    }


}

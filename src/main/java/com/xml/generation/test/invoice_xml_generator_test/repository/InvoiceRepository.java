package com.xml.generation.test.invoice_xml_generator_test.repository;

import com.xml.generation.test.invoice_xml_generator_test.model.entity.Invoice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface InvoiceRepository extends JpaRepository<Invoice,Long> {

    @Query(value = "SELECT * " +
            "FROM invoice" +
            " WHERE QR_CODE IS NOT NULL" +
            " AND user_id IN (SELECT id FROM users) " +
            "AND invoice_number LIKE 'EIN%' " +
//            "AND is_signed = 1"+
            "ORDER BY DBMS_RANDOM.VALUE FETCH FIRST 10 ROWS ONLY", nativeQuery = true)
    List<Invoice> findTenRandom();
}

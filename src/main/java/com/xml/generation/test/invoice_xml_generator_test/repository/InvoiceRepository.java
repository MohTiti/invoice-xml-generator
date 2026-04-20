package com.xml.generation.test.invoice_xml_generator_test.repository;

import com.xml.generation.test.invoice_xml_generator_test.model.data.Invoice;
import jakarta.persistence.QueryHint;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.QueryHints;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

@Repository
public interface InvoiceRepository extends JpaRepository<Invoice, Long> {

    @Query(value = "SELECT * " +
            "FROM invoice" +
            " WHERE QR_CODE IS NOT NULL" +
            " AND user_id IN (SELECT u.id FROM users u WHERE u.taxpayer_id IN (SELECT id FROM tax_payer)) " +
            "AND invoice_number LIKE 'EIN%' " +
//            "AND is_signed = 1"+
            "ORDER BY DBMS_RANDOM.VALUE FETCH FIRST 10 ROWS ONLY", nativeQuery = true)
    List<Invoice> findTenRandom();

    // todo tets file dericet0ry creation
    @Query(value = "SELECT * " +
            "FROM invoice " + // space here
            "WHERE id in (995400,995433,995091,989910,993425,989905,991576,990016,989730,986764) " + // space here
            "ORDER BY DBMS_RANDOM.VALUE FETCH FIRST 10 ROWS ONLY",
            nativeQuery = true)
    List<Invoice> findTen();

    @Query(value = "SELECT * FROM INVOICE   WHERE ID = :invoiceId" ,nativeQuery = true)
    Optional<Invoice> findByIdNative(@Param("invoiceId") Long invoiceId);

////todo edit query
//    @QueryHints(@QueryHint(name = "org.hibernate.fetchSize", value = "1"))
//    @Query(value = "SELECT * " +
//            "FROM invoice" +
//            " WHERE user_id IN (SELECT u.id FROM users u WHERE u.taxpayer_id IN (SELECT id FROM tax_payer)) " +
//            "ORDER BY id ASC", nativeQuery = true)
//    Stream<Invoice> findInvoicesStream();//todo edit query


    @QueryHints(@QueryHint(name = "org.hibernate.fetchSize", value = "1"))
    @Query(value = "SELECT i.* " +
            "FROM tax_payer tp " +
            "JOIN activity a ON a.taxpayer_id = tp.id " +
            "JOIN invoice i ON i.activity_id = a.id " +
            "WHERE tp.tax_number = :taxpayer " +
            "ORDER BY i.id ASC", nativeQuery = true)
    Stream<Invoice> findInvoicesStream(@Param("taxpayer") String taxpayer);


}

package com.xml.generation.test.invoice_xml_generator_test.repository;

import com.xml.generation.test.invoice_xml_generator_test.model.data.Invoice;
import jakarta.persistence.QueryHint;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.QueryHints;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.stream.Stream;

@Repository
public interface InvoiceRepository extends JpaRepository<Invoice, Long> {

    @QueryHints(@QueryHint(name = "org.hibernate.fetchSize", value = "1"))
    @Query(value = "SELECT i.* " +
            "FROM tax_payer tp " +
            "JOIN activity a ON a.taxpayer_id = tp.id " +
            "JOIN invoice i ON i.activity_id = a.id " +
            "WHERE tp.tax_number = :taxpayer " +
            "ORDER BY i.id ASC", nativeQuery = true)
    Stream<Invoice> findInvoicesStream(@Param("taxpayer") String taxpayer);


}

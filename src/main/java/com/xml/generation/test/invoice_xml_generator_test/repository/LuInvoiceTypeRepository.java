package com.xml.generation.test.invoice_xml_generator_test.repository;

import com.xml.generation.test.invoice_xml_generator_test.model.lookup.Lu_InvoiceType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface LuInvoiceTypeRepository extends JpaRepository<Lu_InvoiceType, Long> {
    Optional<Lu_InvoiceType> findByCode(String code);
}
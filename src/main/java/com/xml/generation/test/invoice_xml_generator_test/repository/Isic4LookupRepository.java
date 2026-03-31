package com.xml.generation.test.invoice_xml_generator_test.repository;

import com.xml.generation.test.invoice_xml_generator_test.model.lookup.Isic4Lookup;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface Isic4LookupRepository extends JpaRepository<Isic4Lookup, Long> {
    Optional<Isic4Lookup> findByCode(String code);
}
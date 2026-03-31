package com.xml.generation.test.invoice_xml_generator_test.repository;

import com.xml.generation.test.invoice_xml_generator_test.model.lookup.AdditionalBuyerIdType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AdditionalBuyerIdTypeRepository extends JpaRepository<AdditionalBuyerIdType, Long> {
    Optional<AdditionalBuyerIdType> findByCode(String code);
}
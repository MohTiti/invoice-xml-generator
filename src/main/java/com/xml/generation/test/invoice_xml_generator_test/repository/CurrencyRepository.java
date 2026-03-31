package com.xml.generation.test.invoice_xml_generator_test.repository;

import com.xml.generation.test.invoice_xml_generator_test.model.lookup.Currency;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CurrencyRepository extends JpaRepository<Currency, Long> {
    Optional<Currency> findByCurrencyEnum(String currencyCode);
}
package com.dataart.shipping.repository;

import com.dataart.shipping.domain.Country;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

@SuppressWarnings("unused")
public interface CountryRepository extends CrudRepository<Country, Long> {
    Optional<Country> findByCodeAndActive(String code, Boolean active);
}

package org.gobeshona.api.repository;

import org.gobeshona.api.models.Country;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface CountryRepository extends JpaRepository<Country, Long> {
    Optional<Country> findByCode(String code);
}

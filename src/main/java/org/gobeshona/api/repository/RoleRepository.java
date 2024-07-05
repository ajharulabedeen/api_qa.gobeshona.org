package org.gobeshona.api.repository;

import java.util.Optional;

import org.gobeshona.api.models.ERole;
import org.gobeshona.api.models.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {
  Optional<Role> findByName(ERole name);
}

package com.co.vereda.villalosada.repository;

import com.co.vereda.villalosada.model.Role;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoleRepository extends JpaRepository<Role, Long> {

  Optional<Role> findByNombre(String nombre);
}

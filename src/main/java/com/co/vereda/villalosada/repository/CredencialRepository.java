package com.co.vereda.villalosada.repository;

import com.co.vereda.villalosada.model.Credencial;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CredencialRepository extends JpaRepository<Credencial, Long> {

  Optional<Credencial> findByUsername(String username);
}

package com.co.vereda.villalosada.repository;

import com.co.vereda.villalosada.model.PagoMensual;
import com.co.vereda.villalosada.model.Usuario;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PagoMensualRepository extends JpaRepository<PagoMensual, Long> {

  List<PagoMensual> findByUsuarioAndAnio(Usuario usuario, Integer anio);

  Optional<PagoMensual> findByUsuarioAndAnioAndMes(Usuario usuario, Integer anio, Integer mes);
}

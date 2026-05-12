package com.co.vereda.villalosada.repository;

import com.co.vereda.villalosada.model.Usuario;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

  @Query("SELECT MAX(u.codigoUsuario) FROM Usuario u")
  Long findMaxCodigoUsuario();

  @Query("SELECT DISTINCT u FROM Usuario u LEFT JOIN FETCH u.pagos p WHERE u.rol.nombre <> 'ADMIN'")
  List<Usuario> findUsuariosNoAdmin();

  Optional<Usuario> findByNumeroIdentificacion(String numeroIdentificacion);

  List<Usuario> findByRolNombreNot(String nombreRol);
}

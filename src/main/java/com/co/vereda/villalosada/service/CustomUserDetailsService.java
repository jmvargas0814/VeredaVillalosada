package com.co.vereda.villalosada.service;

import com.co.vereda.villalosada.model.Credencial;
import com.co.vereda.villalosada.repository.CredencialRepository;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class CustomUserDetailsService implements UserDetailsService {

  private final CredencialRepository credencialRepository;

  public CustomUserDetailsService(CredencialRepository credencialRepository) {
    this.credencialRepository = credencialRepository;
  }

  @Override
  public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

    Credencial credencial =
        credencialRepository
            .findByUsername(username)
            .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado"));

    return User.builder()
        .username(credencial.getUsername())
        .password(credencial.getPassword())
        .authorities("ROLE_" + credencial.getUsuario().getRol().getNombre())
        .build();
  }
}

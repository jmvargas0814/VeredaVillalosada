package com.co.vereda.villalosada.controller;

import com.co.vereda.villalosada.model.Credencial;
import com.co.vereda.villalosada.repository.CredencialRepository;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class LoginController {

  private final CredencialRepository credencialRepository;

  public LoginController(CredencialRepository credencialRepository) {
    this.credencialRepository = credencialRepository;
  }

  @GetMapping("/login")
  public String login() {
    return "login";
  }

  @GetMapping("/admin")
  public String admin(Authentication authentication, Model model) {

    if (authentication != null && authentication.isAuthenticated()) {

      String username = authentication.getName();

      Credencial credencial = credencialRepository.findByUsername(username).orElse(null);

      if (credencial != null && credencial.getUsuario() != null) {

        String nombreCompleto =
            credencial.getUsuario().getNombres() + " " + credencial.getUsuario().getApellidos();

        model.addAttribute("nombreCompleto", nombreCompleto);
      }
    }

    return "admin";
  }
}

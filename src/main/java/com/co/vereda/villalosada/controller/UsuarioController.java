package com.co.vereda.villalosada.controller;

import com.co.vereda.villalosada.model.Credencial;
import com.co.vereda.villalosada.model.PagoMensual;
import com.co.vereda.villalosada.model.Role;
import com.co.vereda.villalosada.model.Usuario;
import com.co.vereda.villalosada.repository.CredencialRepository;
import com.co.vereda.villalosada.repository.PagoMensualRepository;
import com.co.vereda.villalosada.repository.RoleRepository;
import com.co.vereda.villalosada.repository.UsuarioRepository;
import com.co.vereda.villalosada.service.ExcelService;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/usuarios")
public class UsuarioController {

  private final UsuarioRepository usuarioRepository;
  private final CredencialRepository credencialRepository;
  private final RoleRepository roleRepository;
  private final PasswordEncoder passwordEncoder;
  private final ExcelService excelService;
  private final PagoMensualRepository pagoMensualRepository;

  public UsuarioController(
      UsuarioRepository usuarioRepository,
      CredencialRepository credencialRepository,
      RoleRepository roleRepository,
      PasswordEncoder passwordEncoder,
      ExcelService excelService,
      PagoMensualRepository pagoMensualRepository) {

    this.usuarioRepository = usuarioRepository;
    this.credencialRepository = credencialRepository;
    this.roleRepository = roleRepository;
    this.passwordEncoder = passwordEncoder;
    this.excelService = excelService;
    this.pagoMensualRepository = pagoMensualRepository;
  }

  @GetMapping("/crear")
  public String mostrarFormulario(Model model) {
    model.addAttribute("usuario", new Usuario());
    return "crearUsuario";
  }

  @PostMapping("/guardar")
  public String guardarUsuario(@ModelAttribute Usuario usuario, Model model) {

    try {

      boolean esEdicion = (usuario.getId() != null);

      if (esEdicion) {

        Usuario usuarioExistente =
            usuarioRepository
                .findById(usuario.getId())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        usuarioExistente.setCorreo(usuario.getCorreo());
        usuarioExistente.setTelefono(usuario.getTelefono());
        usuarioExistente.setEstado(usuario.getEstado());

        usuarioRepository.save(usuarioExistente);

        model.addAttribute("mensajeExito", "✅ Usuario actualizado correctamente");
        model.addAttribute("usuario", usuarioExistente);
        model.addAttribute("modoEdicion", true);

        return "crearUsuario";
      }

      Role rolUser =
          roleRepository.findById(2L).orElseThrow(() -> new RuntimeException("Rol USER no existe"));

      Long ultimoCodigo = usuarioRepository.findMaxCodigoUsuario();
      Long nuevoCodigo = (ultimoCodigo == null) ? 1L : ultimoCodigo + 1;

      usuario.setCodigoUsuario(nuevoCodigo);
      usuario.setRol(rolUser);
      usuario.setFechaRegistro(LocalDateTime.now());
      usuario.setEstado(true);

      String[] nombres = usuario.getNombres().split(" ");
      String[] apellidos = usuario.getApellidos().split(" ");

      String username = nombres[0].toLowerCase() + "." + apellidos[0].toLowerCase();
      String passwordPlano = usuario.getNumeroIdentificacion();

      Usuario usuarioGuardado = usuarioRepository.save(usuario);

      Credencial credencial = new Credencial();
      credencial.setUsername(username);
      credencial.setPassword(passwordEncoder.encode(passwordPlano));
      credencial.setUsuario(usuarioGuardado);
      credencial.setFechaActualizacionPassword(LocalDateTime.now());

      credencialRepository.save(credencial);

      int anioActual = LocalDateTime.now().getYear();
      int mesInicio = usuario.getFechaRegistro().getMonthValue();

      for (int mes = mesInicio; mes <= 12; mes++) {

        PagoMensual pago = new PagoMensual();
        pago.setUsuario(usuarioGuardado);
        pago.setAnio(anioActual);
        pago.setMes(mes);
        pago.setPagado(false);
        pago.setDescripcion("Cuota mensual " + mes);

        pagoMensualRepository.save(pago);
      }

      model.addAttribute("registroExitoso", true);
      model.addAttribute("usernameGenerado", username);
      model.addAttribute("nombreCompleto", usuario.getNombres() + " " + usuario.getApellidos());
      model.addAttribute("usuario", new Usuario());

    } catch (Exception e) {
      model.addAttribute("mensajeError", "Error al procesar usuario ❌");
      e.printStackTrace();
    }

    return "crearUsuario";
  }

  @GetMapping("/consultar")
  public String consultarUsuarios(@RequestParam(required = false) String documento, Model model) {

    List<Usuario> usuarios;

    if (documento != null && !documento.trim().isEmpty()) {
      usuarios =
          usuarioRepository.findByNumeroIdentificacion(documento).map(List::of).orElse(List.of());
    } else {
      usuarios = usuarioRepository.findUsuariosNoAdmin();
    }

    int anioActual = LocalDateTime.now().getYear();

    model.addAttribute("usuarios", usuarios);
    model.addAttribute("anioActual", anioActual);

    return "consultarUsuario";
  }

  @GetMapping("/exportar")
  public ResponseEntity<byte[]> exportarExcel() throws Exception {

    List<Usuario> usuarios = usuarioRepository.findByRolNombreNot("ADMIN");

    byte[] excel = excelService.generarExcelUsuarios(usuarios);

    return ResponseEntity.ok()
        .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=usuarios.xlsx")
        .contentType(MediaType.APPLICATION_OCTET_STREAM)
        .body(excel);
  }

  @GetMapping("/mis-pagos/excel")
  public ResponseEntity<byte[]> exportarExcelUsuario(Authentication authentication)
      throws Exception {

    String username = authentication.getName();

    Credencial credencial =
        credencialRepository
            .findByUsername(username)
            .orElseThrow(() -> new RuntimeException("Credencial no encontrada"));

    Usuario usuario = credencial.getUsuario();

    int anioActual = LocalDateTime.now().getYear();

    List<PagoMensual> pagos = pagoMensualRepository.findByUsuarioAndAnio(usuario, anioActual);

    byte[] excel = excelService.generarExcelPagos(usuario, pagos, anioActual);

    return ResponseEntity.ok()
        .header(
            HttpHeaders.CONTENT_DISPOSITION,
            "attachment; filename=reporte_pagos_" + anioActual + ".xlsx")
        .contentType(MediaType.APPLICATION_OCTET_STREAM)
        .body(excel);
  }

  @GetMapping("/editar/{id}")
  public String editarUsuario(@PathVariable Long id, Model model) {

    Usuario usuario =
        usuarioRepository
            .findById(id)
            .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

    model.addAttribute("usuario", usuario);
    model.addAttribute("modoEdicion", true);

    return "crearUsuario";
  }

  @PostMapping("/toggle-pago")
  public String togglePago(@RequestParam Long pagoId) {

    PagoMensual pago =
        pagoMensualRepository
            .findById(pagoId)
            .orElseThrow(() -> new RuntimeException("Pago no encontrado"));

    pago.setPagado(!pago.getPagado());

    pagoMensualRepository.save(pago);

    return "redirect:/usuarios/consultar";
  }

  @PostMapping("/pagar-mes")
  public String pagarMes(@RequestParam Long pagoId) {

    PagoMensual pago =
        pagoMensualRepository
            .findById(pagoId)
            .orElseThrow(() -> new RuntimeException("Pago no encontrado"));

    pago.setPagado(true);

    pagoMensualRepository.save(pago);

    return "redirect:/usuarios/consultar";
  }

  @GetMapping("/buscar")
  @ResponseBody
  public List<Usuario> buscarUsuarios(@RequestParam String documento) {

    if (documento == null || documento.trim().isEmpty()) {
      return usuarioRepository.findUsuariosNoAdmin();
    }

    return usuarioRepository
            .findByNumeroIdentificacionContainingAndRolNombreNot(documento, "ADMIN");
  }

}

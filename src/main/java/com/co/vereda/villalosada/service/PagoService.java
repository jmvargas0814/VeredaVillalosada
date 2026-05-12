package com.co.vereda.villalosada.service;

import com.co.vereda.villalosada.model.PagoMensual;
import com.co.vereda.villalosada.model.Usuario;
import com.co.vereda.villalosada.repository.PagoMensualRepository;
import com.co.vereda.villalosada.repository.UsuarioRepository;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class PagoService {

  private final UsuarioRepository usuarioRepository;
  private final PagoMensualRepository pagoMensualRepository;

  public PagoService(
      UsuarioRepository usuarioRepository, PagoMensualRepository pagoMensualRepository) {
    this.usuarioRepository = usuarioRepository;
    this.pagoMensualRepository = pagoMensualRepository;
  }

  public void generarPagosUsuariosExistentes() {

    int anioActual = LocalDateTime.now().getYear();

    List<Usuario> usuarios = usuarioRepository.findAll();

    for (Usuario usuario : usuarios) {

      if (usuario.getFechaRegistro() == null) continue;

      int mesInicio = usuario.getFechaRegistro().getMonthValue();

      for (int mes = mesInicio; mes <= 12; mes++) {

        boolean existe =
            pagoMensualRepository.findByUsuarioAndAnioAndMes(usuario, anioActual, mes).isPresent();

        if (!existe) {

          PagoMensual pago = new PagoMensual();
          pago.setUsuario(usuario);
          pago.setAnio(anioActual);
          pago.setMes(mes);
          pago.setPagado(false);
          pago.setDescripcion("Cuota mensual " + mes);

          pagoMensualRepository.save(pago);
        }
      }
    }
  }
}

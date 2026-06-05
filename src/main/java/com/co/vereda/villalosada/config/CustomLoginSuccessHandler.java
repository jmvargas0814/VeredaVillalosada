package com.co.vereda.villalosada.config;

import com.co.vereda.villalosada.model.Credencial;
import com.co.vereda.villalosada.model.LoginRegistro;
import com.co.vereda.villalosada.model.Usuario;
import com.co.vereda.villalosada.repository.CredencialRepository;
import com.co.vereda.villalosada.repository.LoginRegistroRepository;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.LocalDateTime;

@Component
public class CustomLoginSuccessHandler implements AuthenticationSuccessHandler {

    private final LoginRegistroRepository loginRegistroRepository;
    private final CredencialRepository credencialRepository;

    public CustomLoginSuccessHandler(LoginRegistroRepository loginRegistroRepository,
                                     CredencialRepository credencialRepository) {
        this.loginRegistroRepository = loginRegistroRepository;
        this.credencialRepository = credencialRepository;
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication)
            throws IOException {

        String username = authentication.getName();

        Credencial credencial =
                credencialRepository.findByUsername(username).orElse(null);

        if (credencial != null && credencial.getUsuario() != null) {

            Usuario usuario = credencial.getUsuario();

            LoginRegistro registro = new LoginRegistro();

            registro.setUsername(username);
            registro.setNumeroIdentificacion(usuario.getNumeroIdentificacion());
            registro.setNombreCompleto(
                    usuario.getNombres() + " " + usuario.getApellidos()
            );
            registro.setFechaLogin(LocalDateTime.now());
            registro.setIp(request.getRemoteAddr());
            registro.setUsuario(usuario);

            loginRegistroRepository.save(registro);
        }

        response.sendRedirect("/admin");
    }

}

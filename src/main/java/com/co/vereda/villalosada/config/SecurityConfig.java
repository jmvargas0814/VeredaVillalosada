package com.co.vereda.villalosada.config;

import com.co.vereda.villalosada.service.CustomUserDetailsService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

  private final CustomUserDetailsService userDetailsService;

  public SecurityConfig(CustomUserDetailsService userDetailsService) {
    this.userDetailsService = userDetailsService;
  }

  @Bean
  public BCryptPasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
  }

  @Bean
  public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

    http

        //  Autorizacion
        .authorizeHttpRequests(
            auth ->
                auth

                    // Público
                    .requestMatchers("/login")
                    .permitAll()

                    // Solo ADMIN
                    .requestMatchers("/usuarios/crear", "/usuarios/consultar")
                    .hasRole("ADMIN")

                    // Solo USER
                    .requestMatchers("/usuarios/mis-pagos/**")
                    .hasRole("USER")

                    // ADMIN y USER
                    .requestMatchers("/usuarios/editar/**")
                    .hasAnyRole("ADMIN", "USER")

                    // Todo lo demás requiere autenticación
                    .anyRequest()
                    .authenticated())

        //  login personalizado
        .formLogin(form -> form.loginPage("/login").defaultSuccessUrl("/admin", true).permitAll())

        // legeo seguro por post
        .logout(
            logout ->
                logout
                    .logoutUrl("/logout")
                    .logoutSuccessUrl("/login?logout")
                    .invalidateHttpSession(true)
                    .deleteCookies("JSESSIONID")
                    .permitAll())

        // Control de session
        .sessionManagement(session -> session.maximumSessions(1))

        // Evitar guardar cache
        .headers(headers -> headers.cacheControl(cache -> {}));

    return http.build();
  }
}

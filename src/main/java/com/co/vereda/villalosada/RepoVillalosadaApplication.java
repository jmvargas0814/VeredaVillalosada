package com.co.vereda.villalosada;

import com.co.vereda.villalosada.service.PagoService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class RepoVillalosadaApplication {

  public static void main(String[] args) {
    SpringApplication.run(RepoVillalosadaApplication.class, args);
  }

  @Bean
  CommandLineRunner initPagos(PagoService pagoService) {
    return args -> {
      pagoService.generarPagosUsuariosExistentes();
    };
  }
}

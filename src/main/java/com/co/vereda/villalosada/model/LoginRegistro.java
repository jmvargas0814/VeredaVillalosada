package com.co.vereda.villalosada.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "login_registros")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class LoginRegistro {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String username;

    private LocalDateTime fechaLogin;

    private String numeroIdentificacion;

    private String nombreCompleto;

    private String ip;

    @ManyToOne
    @JoinColumn(name = "usuario_id")
    private Usuario usuario;
}

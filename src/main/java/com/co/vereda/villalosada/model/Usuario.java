package com.co.vereda.villalosada.model;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "usuarios")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Usuario {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(name = "codigo_usuario", unique = true, nullable = false)
  private Long codigoUsuario;

  @Column(unique = true)
  private String numeroIdentificacion;

  private String nombres;
  private String apellidos;
  private String correo;
  private String telefono;
  private LocalDate fechaNacimiento;
  private LocalDateTime fechaRegistro;

  private Boolean estado;

  @ManyToOne
  @JoinColumn(name = "rol_id")
  private Role rol;

  @OneToMany(mappedBy = "usuario", cascade = CascadeType.ALL)
  private List<PagoMensual> pagos;
}

package com.co.vereda.villalosada.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "pagos_mensuales")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PagoMensual {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  private Integer anio;

  private Integer mes;

  private Boolean pagado = false;

  private String descripcion;

  @ManyToOne
  @JoinColumn(name = "usuario_id")
  private Usuario usuario;
}

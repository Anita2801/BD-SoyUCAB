package com.soyucab.back.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.time.LocalDate;

@Entity
@Table(name = "Tiempo_Duracion")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TiempoDuracion {

    @EmbeddedId
    private TiempoDuracionId id;

    @ManyToOne
    @MapsId("instT")
    @JoinColumn(name = "Inst_T")
    private EntidadInstitucional institucion;

    @ManyToOne
    @MapsId("ciT")
    @JoinColumn(name = "CI_T")
    private Persona persona;

    @ManyToOne
    @MapsId("rolT")
    @JoinColumn(name = "Rol_T")
    private Rol rol;

    @Column(name = "Fecha_Inicio", nullable = false)
    private LocalDate fechaInicio;

    @Column(name = "Fecha_Fin")
    private LocalDate fechaFin;
}

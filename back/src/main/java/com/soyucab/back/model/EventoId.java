package com.soyucab.back.model;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.io.Serializable;
import java.time.LocalDate;

@Embeddable
@Data
@NoArgsConstructor
@AllArgsConstructor
public class EventoId implements Serializable {
    @Column(name = "Nombre_Evento", length = 30)
    private String nombre;

    @Column(name = "Fecha_Evento")
    private LocalDate fecha;

    @Column(name = "Usuario_Evento", length = 15)
    private String usuarioOrganizador;
}

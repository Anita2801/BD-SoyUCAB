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
public class AsistenciaEventoId implements Serializable {
    @Column(name = "Usuario_Asistente", length = 15)
    private String usuarioAsistente;

    @Column(name = "Nombre_Evento", length = 30)
    private String nombreEvento;

    @Column(name = "Fecha_Evento")
    private LocalDate fechaEvento;

    @Column(name = "Usuario_Evento_Org", length = 15)
    private String usuarioOrganizador;
}

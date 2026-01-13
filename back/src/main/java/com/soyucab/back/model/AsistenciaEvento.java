package com.soyucab.back.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Table(name = "Asistencia_Evento")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AsistenciaEvento {

    @EmbeddedId
    private AsistenciaEventoId id;

    @ManyToOne
    @MapsId("usuarioAsistente")
    @JoinColumn(name = "Usuario_Asistente")
    private Usuario asistente;

    @ManyToOne
    @JoinColumns({
            @JoinColumn(name = "Nombre_Evento", referencedColumnName = "Nombre_Evento", insertable = false, updatable = false),
            @JoinColumn(name = "Fecha_Evento", referencedColumnName = "Fecha_Evento", insertable = false, updatable = false),
            @JoinColumn(name = "Usuario_Evento_Org", referencedColumnName = "Usuario_Evento", insertable = false, updatable = false)
    })
    private Evento evento;

    @Column(name = "Fecha_Registro")
    private LocalDateTime fechaRegistro;

    @Column(name = "Asistencia_Confirmada")
    private Boolean confirmada = true;
}

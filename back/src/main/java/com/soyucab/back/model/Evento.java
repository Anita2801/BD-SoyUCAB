package com.soyucab.back.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Entity
@Table(name = "Evento")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Evento {

    @EmbeddedId
    private EventoId id;

    @ManyToOne
    @MapsId("usuarioOrganizador")
    @JoinColumn(name = "Usuario_Evento")
    private Usuario organizador;

    @Column(name = "Desc_Evento", length = 200)
    private String descripcion;

    @OneToMany(mappedBy = "evento", fetch = FetchType.LAZY)
    private java.util.List<AsistenciaEvento> asistencias;
}

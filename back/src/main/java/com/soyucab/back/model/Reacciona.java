package com.soyucab.back.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Entity
@Table(name = "Reacciona")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Reacciona {

    @EmbeddedId
    private ReaccionaId id;

    @ManyToOne
    @MapsId("usuarioReacciona")
    @JoinColumn(name = "Usuario_Reacciona")
    private Usuario usuario;

    @ManyToOne
    @JoinColumns({
            @JoinColumn(name = "Usuario_Contenido", referencedColumnName = "Usuario_Creador", insertable = false, updatable = false),
            @JoinColumn(name = "fechahora_contenido", referencedColumnName = "fechahora_creacion", insertable = false, updatable = false)
    })
    private Contenido contenido;

    @Column(name = "Reaccion", nullable = false, length = 15)
    private String reaccion;
}

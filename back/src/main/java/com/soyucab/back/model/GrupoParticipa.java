package com.soyucab.back.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Entity
@Table(name = "Grupo_Participa")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class GrupoParticipa {

    @EmbeddedId
    private GrupoParticipaId id;

    @ManyToOne
    @MapsId("nombreGrupo")
    @JoinColumn(name = "Nombre_Grupo")
    private Grupo grupo;

    @ManyToOne
    @MapsId("usuarioMiembro")
    @JoinColumn(name = "Usuario_Miembro")
    private Usuario usuario;

    @Column(name = "Rol_Miembro", nullable = false, length = 15)
    private String rol;
}

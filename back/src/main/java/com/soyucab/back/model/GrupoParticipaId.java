package com.soyucab.back.model;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.io.Serializable;

@Embeddable
@Data
@NoArgsConstructor
@AllArgsConstructor
public class GrupoParticipaId implements Serializable {
    @Column(name = "Nombre_Grupo", length = 30)
    private String nombreGrupo;

    @Column(name = "Usuario_Miembro", length = 15)
    private String usuarioMiembro;
}

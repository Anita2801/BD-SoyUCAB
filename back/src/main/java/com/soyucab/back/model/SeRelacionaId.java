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
public class SeRelacionaId implements Serializable {
    @Column(name = "Usuario_Receptor", length = 15)
    private String usuarioReceptor;

    @Column(name = "Usuario_Solicitante", length = 15)
    private String usuarioSolicitante;
}

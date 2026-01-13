package com.soyucab.back.model;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.io.Serializable;
import java.time.LocalDateTime;

@Embeddable
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ContenidoId implements Serializable {
    @Column(name = "Usuario_Creador", length = 15)
    private String usuarioCreador;

    @Column(name = "fechahora_creacion")
    private LocalDateTime fechaHoraCreacion;
}

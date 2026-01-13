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
public class ReaccionaId implements Serializable {
    @Column(name = "Usuario_Reacciona", length = 15)
    private String usuarioReacciona;

    @Column(name = "Usuario_Contenido", length = 15)
    private String usuarioContenido;

    @Column(name = "fechahora_contenido")
    private LocalDateTime fechaHoraContenido;
}

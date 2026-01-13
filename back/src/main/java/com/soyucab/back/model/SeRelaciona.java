package com.soyucab.back.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.time.LocalDate;

@Entity
@Table(name = "Se_Relaciona")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SeRelaciona {

    @EmbeddedId
    private SeRelacionaId id;

    @ManyToOne
    @MapsId("usuarioReceptor")
    @JoinColumn(name = "Usuario_Receptor")
    private Usuario receptor;

    @ManyToOne
    @MapsId("usuarioSolicitante")
    @JoinColumn(name = "Usuario_Solicitante")
    private Usuario solicitante;

    @Column(name = "Estado", nullable = false, length = 15)
    private String estado = "Pendiente";

    @Column(name = "Tipo_Relacion", nullable = false, length = 15)
    private String tipoRelacion;

    @Column(name = "Fecha_Relacion", nullable = false)
    private LocalDate fechaRelacion;
}

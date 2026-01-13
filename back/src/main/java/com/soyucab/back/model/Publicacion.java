package com.soyucab.back.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.time.LocalDate;

@Entity
@Table(name = "Publicacion")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Publicacion {

    @EmbeddedId
    private PublicacionId id;

    @ManyToOne
    @MapsId("autor")
    @JoinColumn(name = "Autor")
    private Usuario usuario;

    @Column(name = "Sinopsis_Pub", nullable = false, length = 400)
    private String sinopsis;

    @Column(name = "Fecha_Pub", nullable = false)
    private LocalDate fecha;
}

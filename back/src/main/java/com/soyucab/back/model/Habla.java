package com.soyucab.back.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Entity
@Table(name = "Habla")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Habla {

    @EmbeddedId
    private HablaId id;

    @ManyToOne
    @MapsId("ciPersona")
    @JoinColumn(name = "CI_Persona")
    private Persona persona;

    @ManyToOne
    @MapsId("isoIdioma")
    @JoinColumn(name = "ISO_Idioma")
    private Idioma idioma;

    @Column(name = "Nivel_Fluidez", length = 20)
    private String nivelFluidez;
}

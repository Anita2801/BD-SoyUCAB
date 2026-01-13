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
public class HablaId implements Serializable {
    @Column(name = "CI_Persona", length = 15)
    private String ciPersona;

    @Column(name = "ISO_Idioma", length = 3)
    private String isoIdioma;
}

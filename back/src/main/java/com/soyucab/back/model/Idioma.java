package com.soyucab.back.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Entity
@Table(name = "Idioma")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Idioma {

    @Id
    @Column(name = "ISO_Idioma", length = 3)
    private String isoIdioma;

    @Column(name = "Idioma", nullable = false, length = 12)
    private String idioma;
}

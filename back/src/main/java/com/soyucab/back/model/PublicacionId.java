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
public class PublicacionId implements Serializable {
    @Column(name = "Titulo_Pub", length = 50)
    private String titulo;

    @Column(name = "Autor", length = 15)
    private String autor;
}

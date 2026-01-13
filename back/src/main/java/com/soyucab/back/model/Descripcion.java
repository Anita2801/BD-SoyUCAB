package com.soyucab.back.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Entity
@Table(name = "Descripcion")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Descripcion {

    @EmbeddedId
    private DescripcionId id;

    @ManyToOne
    @MapsId("cuentaDesc")
    @JoinColumn(name = "Cuenta_Desc")
    private Usuario usuario;
}

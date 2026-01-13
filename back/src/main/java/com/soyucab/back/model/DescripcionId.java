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
public class DescripcionId implements Serializable {
    @Column(name = "Descripcion", length = 200)
    private String descripcion;

    @Column(name = "Cuenta_Desc", length = 15)
    private String cuentaDesc;
}

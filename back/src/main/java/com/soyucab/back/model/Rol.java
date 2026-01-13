package com.soyucab.back.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Entity
@Table(name = "Rol")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Rol {

    @Id
    @Column(name = "Tipo_Rol", length = 20)
    private String tipoRol;
}

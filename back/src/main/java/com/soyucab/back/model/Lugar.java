package com.soyucab.back.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Entity
@Table(name = "Lugar")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Lugar {

    @Id
    @Column(name = "ISO_Lugar", length = 10)
    private String isoLugar;

    @Column(name = "Nombre_L", nullable = false, length = 20)
    private String nombre;

    @Column(name = "Tipo_L", nullable = false, length = 15)
    private String tipo;

    @ManyToOne
    @JoinColumn(name = "ISO_Superior")
    private Lugar lugarSuperior;
}

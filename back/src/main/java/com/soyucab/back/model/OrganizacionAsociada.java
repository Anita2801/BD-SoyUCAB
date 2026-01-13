package com.soyucab.back.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Entity
@Table(name = "Organizacion_Asociada")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrganizacionAsociada {

    @Id
    @Column(name = "RIF", length = 15)
    private String rif;

    @OneToOne
    @JoinColumn(name = "OA_Usuario", unique = true, nullable = false)
    private Usuario usuario;

    @Column(name = "Nombre_O", nullable = false, length = 30)
    private String nombre;

    @ManyToOne
    @JoinColumn(name = "Ubicacion_OA", nullable = false)
    private Lugar lugar;
}

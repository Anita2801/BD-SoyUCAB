package com.soyucab.back.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Entity
@Table(name = "Entidad_Institucional")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class EntidadInstitucional {

    @Id
    @Column(name = "Cod_Inst", length = 10)
    private String codInst;

    @Column(name = "Nombre_Ent_Inst", nullable = false, length = 50)
    private String nombre;

    @ManyToOne
    @JoinColumn(name = "Cod_Inst_Sup")
    private EntidadInstitucional institucionSuperior;
}

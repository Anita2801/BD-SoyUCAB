package com.soyucab.back.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Entity
@Table(name = "Nexo")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Nexo {

    @EmbeddedId
    private NexoId id;

    @ManyToOne(cascade = CascadeType.ALL)
    @MapsId("codInstNexo")
    @JoinColumn(name = "Cod_Inst_Nexo")
    private EntidadInstitucional institucion;

    @ManyToOne(cascade = CascadeType.ALL)
    @MapsId("ciNexo")
    @JoinColumn(name = "CI_Nexo")
    private Persona persona;
}

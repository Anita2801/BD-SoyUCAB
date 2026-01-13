package com.soyucab.back.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.util.List;

@Entity
@Table(name = "Grupo")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Grupo {

    @Id
    @Column(name = "Nombre_Grupo", length = 30)
    private String nombre;

    @Column(name = "Desc_Grupo", length = 300)
    private String descripcion;

    @Column(name = "Tipo_Grupo", length = 12)
    private String tipo;

    @OneToMany(mappedBy = "grupo")
    private List<GrupoParticipa> miembros;
}

package com.soyucab.back.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Entity
@Table(name = "Contenido")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Contenido {

    @EmbeddedId
    private ContenidoId id;

    @ManyToOne
    @MapsId("usuarioCreador")
    @JoinColumn(name = "Usuario_Creador")
    private Usuario usuario;

    @Column(name = "Cuerpo_Contenido", length = 300)
    private String cuerpo;

    @Column(name = "Nro_Me_Gusta")
    private Integer meGusta = 0;

    @Column(name = "Nro_No_Me_Gusta")
    private Integer noMeGusta = 0;
}

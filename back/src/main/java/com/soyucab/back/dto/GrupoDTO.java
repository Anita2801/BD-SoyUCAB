package com.soyucab.back.dto;

import lombok.Data;

@Data
public class GrupoDTO {
    private String nombre;
    private String descripcion;
    private Boolean esPrivado;
    private Long numeroMiembros;
    private String myRole; // "Creador" or "Miembro"
    private String creadorNombre; // Nombre del usuario que creó el grupo
}

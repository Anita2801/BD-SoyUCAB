package com.soyucab.back.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RegisterRequest {
    private String cuenta;
    private String password;
    private String email;

    // Datos personales
    private String ci;
    private String primerNombre;
    private String segundoNombre;
    private String primerApellido;
    private String segundoApellido;
    private String sexo;
    private String lugarIso;
    private String rol;

    // Datos de Nexo
    private String entidadCodigo;
    private String fechaInicio;
    private String fechaFin;
}

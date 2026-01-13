package com.soyucab.back.dto;

import lombok.Data;

@Data
public class PersonaDTO {
    private String ci;
    private String usuarioCuenta;
    private String primerNombre;
    private String segundoNombre;
    private String primerApellido;
    private String segundoApellido;
    private String sexo;
    private String lugarNombre;
    private String lugarIso;
}

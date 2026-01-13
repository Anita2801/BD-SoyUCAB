package com.soyucab.back.dto;

import lombok.Data;

@Data
public class ProfileUpdateDTO {
    private String primerNombre;
    private String segundoNombre;
    private String primerApellido;
    private String segundoApellido;
    private String bio;
    private String phone;
    private String location;
    private String sexo;
}

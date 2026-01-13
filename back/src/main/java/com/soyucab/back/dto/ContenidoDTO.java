package com.soyucab.back.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class ContenidoDTO {
    private String usuarioCreador;
    private LocalDateTime fechaHoraCreacion;
    private String cuerpo;
    private Integer meGusta;
    private Integer noMeGusta;
    private String authorName; // Full Name of the creator
}

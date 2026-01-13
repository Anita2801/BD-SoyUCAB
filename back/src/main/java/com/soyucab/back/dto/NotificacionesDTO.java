package com.soyucab.back.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class NotificacionesDTO {
    private Integer id;
    private String usuarioDestino; // Cuenta
    private String mensaje;
    private String tipoAlerta;
    private LocalDateTime fechaCreacion;
}

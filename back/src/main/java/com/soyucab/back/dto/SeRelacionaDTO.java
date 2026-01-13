package com.soyucab.back.dto;

import lombok.Data;
import java.time.LocalDate;

@Data
public class SeRelacionaDTO {
    private String usuarioReceptor;
    private String usuarioSolicitante;
    private String estado;
    private String tipoRelacion;
    private LocalDate fechaRelacion;
}

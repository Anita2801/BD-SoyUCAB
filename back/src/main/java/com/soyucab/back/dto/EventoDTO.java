package com.soyucab.back.dto;

import lombok.Data;
import java.time.LocalDate;

@Data
public class EventoDTO {
    private String nombre;
    private LocalDate fecha;
    private String usuarioOrganizador;
    private String descripcion;

    // Frontend-only fields (mapped or defaults)
    private int attendees;
    private String time;
    private String location;
    private String category;
    private java.util.List<String> attendeesList; // List of participant usernames
}

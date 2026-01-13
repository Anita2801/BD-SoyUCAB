package com.soyucab.back.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "Chat")
@IdClass(ChatId.class)
public class Chat {

    @Id
    @Column(name = "Nombre_Chat", length = 30, nullable = false)
    private String nombreChat;

    @Id
    @Column(name = "Fecha_Creacion_Chat", nullable = false)
    private LocalDateTime fechaCreacionChat;

    public Chat() {
    }

    public String getNombreChat() {
        return nombreChat;
    }

    public void setNombreChat(String nombreChat) {
        this.nombreChat = nombreChat;
    }

    public LocalDateTime getFechaCreacionChat() {
        return fechaCreacionChat;
    }

    public void setFechaCreacionChat(LocalDateTime fechaCreacionChat) {
        this.fechaCreacionChat = fechaCreacionChat;
    }
}

package com.soyucab.back.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "Mensaje")
public class Mensaje {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(columnDefinition = "TEXT")
    private String contenido;

    private LocalDateTime fecha;

    @ManyToOne
    @JoinColumn(name = "sender_id")
    private Usuario sender;

    @ManyToOne
    @JoinColumns({
            @JoinColumn(name = "chat_timestamp", referencedColumnName = "Fecha_Creacion_Chat"),
            @JoinColumn(name = "chat_nombre", referencedColumnName = "Nombre_Chat")
    })
    private Chat chat;

    public Mensaje() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getContenido() {
        return contenido;
    }

    public void setContenido(String contenido) {
        this.contenido = contenido;
    }

    public LocalDateTime getFecha() {
        return fecha;
    }

    public void setFecha(LocalDateTime fecha) {
        this.fecha = fecha;
    }

    public Usuario getSender() {
        return sender;
    }

    public void setSender(Usuario sender) {
        this.sender = sender;
    }

    public Chat getChat() {
        return chat;
    }

    public void setChat(Chat chat) {
        this.chat = chat;
    }
}

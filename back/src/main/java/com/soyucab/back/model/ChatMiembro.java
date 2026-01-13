package com.soyucab.back.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "Chat_Miembro")
@IdClass(ChatMiembroId.class)
public class ChatMiembro {

    @Id
    @Column(name = "Chat_Participa", length = 30, nullable = false)
    private String chatParticipa;

    @Id
    @Column(name = "Fecha_Chat", nullable = false)
    private LocalDateTime fechaChat;

    @Id
    @Column(name = "Usuario_Chat", length = 15, nullable = false)
    private String usuarioChat;

    @Column(name = "Rol_Chat", length = 15, nullable = false)
    private String rolChat;

    @ManyToOne
    @JoinColumns({
            @JoinColumn(name = "Chat_Participa", referencedColumnName = "Nombre_Chat", insertable = false, updatable = false),
            @JoinColumn(name = "Fecha_Chat", referencedColumnName = "Fecha_Creacion_Chat", insertable = false, updatable = false)
    })
    private Chat chat;

    @ManyToOne
    @JoinColumn(name = "Usuario_Chat", referencedColumnName = "Cuenta", insertable = false, updatable = false)
    private Usuario usuario;

    public ChatMiembro() {
    }

    public String getChatParticipa() {
        return chatParticipa;
    }

    public void setChatParticipa(String chatParticipa) {
        this.chatParticipa = chatParticipa;
    }

    public LocalDateTime getFechaChat() {
        return fechaChat;
    }

    public void setFechaChat(LocalDateTime fechaChat) {
        this.fechaChat = fechaChat;
    }

    public String getUsuarioChat() {
        return usuarioChat;
    }

    public void setUsuarioChat(String usuarioChat) {
        this.usuarioChat = usuarioChat;
    }

    public String getRolChat() {
        return rolChat;
    }

    public void setRolChat(String rolChat) {
        this.rolChat = rolChat;
    }

    public Chat getChat() {
        return chat;
    }

    public void setChat(Chat chat) {
        this.chat = chat;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }
}

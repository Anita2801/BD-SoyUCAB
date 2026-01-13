package com.soyucab.back.model;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Objects;

public class ChatMiembroId implements Serializable {
    private String chatParticipa;
    private LocalDateTime fechaChat;
    private String usuarioChat;

    public ChatMiembroId() {
    }

    public ChatMiembroId(String chatParticipa, LocalDateTime fechaChat, String usuarioChat) {
        this.chatParticipa = chatParticipa;
        this.fechaChat = fechaChat;
        this.usuarioChat = usuarioChat;
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

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        ChatMiembroId that = (ChatMiembroId) o;
        return Objects.equals(chatParticipa, that.chatParticipa) &&
                Objects.equals(fechaChat, that.fechaChat) &&
                Objects.equals(usuarioChat, that.usuarioChat);
    }

    @Override
    public int hashCode() {
        return Objects.hash(chatParticipa, fechaChat, usuarioChat);
    }
}

package com.soyucab.back.model;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Objects;

public class ChatId implements Serializable {
    private String nombreChat;
    private LocalDateTime fechaCreacionChat;

    public ChatId() {
    }

    public ChatId(String nombreChat, LocalDateTime fechaCreacionChat) {
        this.nombreChat = nombreChat;
        this.fechaCreacionChat = fechaCreacionChat;
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

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        ChatId chatId = (ChatId) o;
        return Objects.equals(nombreChat, chatId.nombreChat) &&
                Objects.equals(fechaCreacionChat, chatId.fechaCreacionChat);
    }

    @Override
    public int hashCode() {
        return Objects.hash(nombreChat, fechaCreacionChat);
    }
}

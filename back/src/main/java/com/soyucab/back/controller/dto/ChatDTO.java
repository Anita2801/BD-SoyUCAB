package com.soyucab.back.controller.dto;

import java.time.LocalDateTime;
import java.util.List;

public class ChatDTO {
    private String nombre;
    private String fechaCreacion;
    private String lastMessage;
    private String lastMessageTime;
    private List<MessageDTO> messages;

    public ChatDTO(String nombre, java.time.LocalDateTime fechaCreacion) {
        this.nombre = nombre;
        this.fechaCreacion = fechaCreacion.toString();
    }

    // Getters and Setters
    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getFechaCreacion() {
        return fechaCreacion;
    }

    public void setFechaCreacion(String fechaCreacion) {
        this.fechaCreacion = fechaCreacion;
    }

    public String getLastMessage() {
        return lastMessage;
    }

    public void setLastMessage(String lastMessage) {
        this.lastMessage = lastMessage;
    }

    public String getLastMessageTime() {
        return lastMessageTime;
    }

    public void setLastMessageTime(java.time.LocalDateTime lastMessageTime) {
        this.lastMessageTime = lastMessageTime != null ? lastMessageTime.toString() : null;
    }

    public List<MessageDTO> getMessages() {
        return messages;
    }

    public void setMessages(List<MessageDTO> messages) {
        this.messages = messages;
    }

    public static class MessageDTO {
        private String sender;
        private String senderName;
        private String content;
        private LocalDateTime time;
        private boolean isMine;

        public MessageDTO(String sender, String senderName, String content, LocalDateTime time, boolean isMine) {
            this.sender = sender;
            this.senderName = senderName;
            this.content = content;
            this.time = time;
            this.isMine = isMine;
        }

        @com.fasterxml.jackson.annotation.JsonProperty("senderName")
        public String getSenderName() {
            return senderName;
        }

        public String getSender() {
            return sender;
        }

        public String getContent() {
            return content;
        }

        public LocalDateTime getTime() {
            return time;
        }

        public boolean isMine() {
            return isMine;
        }
    }
}

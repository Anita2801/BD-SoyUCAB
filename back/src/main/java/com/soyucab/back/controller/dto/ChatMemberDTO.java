package com.soyucab.back.controller.dto;

public class ChatMemberDTO {
    private String userId;
    private String fullName;
    private String role;
    private String email;

    public ChatMemberDTO(String userId, String fullName, String role, String email) {
        this.userId = userId;
        this.fullName = fullName;
        this.role = role;
        this.email = email;
    }

    @com.fasterxml.jackson.annotation.JsonProperty("userId")
    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    @com.fasterxml.jackson.annotation.JsonProperty("fullName")
    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    @com.fasterxml.jackson.annotation.JsonProperty("role")
    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    @com.fasterxml.jackson.annotation.JsonProperty("email")
    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}

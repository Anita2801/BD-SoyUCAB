package com.soyucab.back.dto;

import lombok.Data;

@Data
public class ProfileUpdateDTO {
    private String primerNombre;
    private String segundoNombre;
    private String primerApellido;
    private String segundoApellido;
    private String bio;
    private String phone;
    private String location;
    private String sexo;
    private java.util.List<ExperienceDTO> experience;
    private java.util.List<LanguageDTO> languages;

    @Data
    public static class ExperienceDTO {
        private String role;
        private String company;
        private String startDate; // ISO Date YYYY-MM-DD
        private String endDate; // ISO Date YYYY-MM-DD or null
        private String description;
    }

    @Data
    public static class LanguageDTO {
        private String name; // Language name e.g. "Inglés"
        private int level; // 0-100
    }
}

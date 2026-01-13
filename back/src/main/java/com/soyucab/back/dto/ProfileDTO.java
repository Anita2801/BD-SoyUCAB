package com.soyucab.back.dto;

import lombok.Data;
import java.util.List;

@Data
public class ProfileDTO {
    private String name;
    private String primerNombre;
    private String segundoNombre;
    private String primerApellido;
    private String segundoApellido;
    private String role;
    private String semester;
    private String location;
    private String email;
    private String phone;
    private String bio;
    private String sexo;
    private StatsDTO stats;
    private List<LanguageDTO> languages;
    private List<ExperienceDTO> experience;
    private List<ContactDTO> contacts;

    @Data
    public static class StatsDTO {
        private int connections;
        private int posts;
    }

    @Data
    public static class LanguageDTO {
        private String name;
        private int level; // 0-100
    }

    @Data
    public static class ExperienceDTO {
        private String role;
        private String company;
        private String period;
        private String description;
    }

    @Data
    public static class ContactDTO {
        private String name;
        private String role;
        private String initials;
        private String color;
    }
}

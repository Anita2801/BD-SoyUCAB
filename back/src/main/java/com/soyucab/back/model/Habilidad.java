package com.soyucab.back.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Entity
@Table(name = "Habilidad")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Habilidad {

    @Id
    @Column(name = "Habilidad", length = 30)
    private String nombre;
}

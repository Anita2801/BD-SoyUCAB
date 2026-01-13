package com.soyucab.back.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.util.List;

@Entity
@Table(name = "Persona")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Persona {

    @Id
    @Column(name = "CI", length = 15)
    private String ci;

    @OneToOne
    @JoinColumn(name = "Persona_Usuario", unique = true, nullable = false)
    private Usuario usuario;

    @Column(name = "Primer_Nombre", nullable = false, length = 15)
    private String primerNombre;

    @Column(name = "Segundo_Nombre", length = 15)
    private String segundoNombre;

    @Column(name = "Primer_Apellido", nullable = false, length = 15)
    private String primerApellido;

    @Column(name = "Segundo_Apellido", nullable = false, length = 15)
    private String segundoApellido;

    @Column(name = "Sexo", nullable = false, length = 10)
    private String sexo;

    @ManyToOne
    @JoinColumn(name = "Ubicacion_Persona", nullable = false)
    private Lugar lugar;

    @OneToMany(mappedBy = "persona")
    private List<Habla> idiomas;

    @OneToMany(mappedBy = "persona")
    private List<TiempoDuracion> trayectoria;

    @ManyToMany
    @JoinTable(name = "Capacidad_Hab", joinColumns = @JoinColumn(name = "CI"), inverseJoinColumns = @JoinColumn(name = "Habilidad"))
    private List<Habilidad> habilidades;

    @ManyToMany
    @JoinTable(name = "Desempeña", joinColumns = @JoinColumn(name = "CI_Rol"), inverseJoinColumns = @JoinColumn(name = "Des_Rol"))
    private List<Rol> roles;

    @ManyToMany
    @JoinTable(name = "Nexo", joinColumns = @JoinColumn(name = "CI_Nexo"), inverseJoinColumns = @JoinColumn(name = "Cod_Inst_Nexo"))
    private List<EntidadInstitucional> instituciones;
}

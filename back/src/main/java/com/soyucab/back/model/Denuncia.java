package com.soyucab.back.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.time.LocalDate;

@Entity
@Table(name = "Denuncia")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Denuncia {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_denuncia")
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "usuario_denunciante", nullable = false)
    private Usuario denunciante;

    @ManyToOne
    @JoinColumn(name = "usuario_denunciado", nullable = false)
    private Usuario denunciado;

    @Column(name = "motivo", length = 100)
    private String motivo;

    @Column(name = "fecha_denuncia", insertable = false, updatable = false)
    private LocalDate fechaDenuncia;

    @Column(name = "estatus", length = 20)
    private String estatus = "Pendiente";
}

package com.soyucab.back.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Table(name = "Notificaciones")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Notificaciones {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_notificacion")
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "id_usuario_destino", nullable = false)
    private Usuario usuarioDestino;

    @Column(name = "mensaje", nullable = false, columnDefinition = "TEXT")
    private String mensaje;

    @Column(name = "tipo_alerta", nullable = false, length = 20)
    private String tipoAlerta;

    @Column(name = "fecha_creacion", insertable = false, updatable = false)
    private LocalDateTime fechaCreacion;
}

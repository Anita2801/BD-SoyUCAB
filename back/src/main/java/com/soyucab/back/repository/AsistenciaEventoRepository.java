package com.soyucab.back.repository;

import com.soyucab.back.model.AsistenciaEvento;
import com.soyucab.back.model.AsistenciaEventoId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDate;

@Repository
public interface AsistenciaEventoRepository extends JpaRepository<AsistenciaEvento, AsistenciaEventoId> {
    
    @Modifying
    @Transactional
    @Query("DELETE FROM AsistenciaEvento a WHERE a.id.nombreEvento = :nombre AND a.id.fechaEvento = :fecha AND a.id.usuarioOrganizador = :organizador")
    void deleteByEventoId(@Param("nombre") String nombre,
                          @Param("fecha") LocalDate fecha,
                          @Param("organizador") String organizador);
}

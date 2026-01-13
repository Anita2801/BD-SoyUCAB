package com.soyucab.back.repository;

import com.soyucab.back.model.Notificaciones;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NotificacionesRepository extends JpaRepository<Notificaciones, Integer> {
    List<Notificaciones> findByUsuarioDestino_Cuenta(String cuenta);
}

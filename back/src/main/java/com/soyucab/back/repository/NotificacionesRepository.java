package com.soyucab.back.repository;

import com.soyucab.back.model.Notificaciones;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface NotificacionesRepository extends JpaRepository<Notificaciones, Long> {

    List<Notificaciones> findByUsuarioDestino_Cuenta(String cuenta);

    List<Notificaciones> findByUsuarioDestino_CuentaOrderByFechaCreacionDesc(String cuenta);

    @Modifying
    @Query("DELETE FROM Notificaciones n WHERE n.usuarioDestino.cuenta = :cuenta")
    void deleteByUsuarioDestinoCuenta(@Param("cuenta") String cuenta);
}

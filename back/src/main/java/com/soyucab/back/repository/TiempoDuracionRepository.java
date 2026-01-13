package com.soyucab.back.repository;

import com.soyucab.back.model.TiempoDuracion;
import com.soyucab.back.model.TiempoDuracionId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TiempoDuracionRepository extends JpaRepository<TiempoDuracion, TiempoDuracionId> {
    List<TiempoDuracion> findByPersona_Usuario_Cuenta(String cuenta);
}

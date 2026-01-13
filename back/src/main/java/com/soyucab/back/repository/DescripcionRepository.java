package com.soyucab.back.repository;

import com.soyucab.back.model.Descripcion;
import com.soyucab.back.model.DescripcionId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DescripcionRepository extends JpaRepository<Descripcion, DescripcionId> {
    java.util.List<Descripcion> findByUsuario_Cuenta(String cuenta);
}

package com.soyucab.back.repository;

import com.soyucab.back.model.Publicacion;
import com.soyucab.back.model.PublicacionId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PublicacionRepository extends JpaRepository<Publicacion, PublicacionId> {
    long countByUsuario_Cuenta(String cuenta);
}

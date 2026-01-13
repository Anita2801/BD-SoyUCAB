package com.soyucab.back.repository;

import com.soyucab.back.model.Contenido;
import com.soyucab.back.model.ContenidoId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ContenidoRepository extends JpaRepository<Contenido, ContenidoId> {
    @org.springframework.data.jpa.repository.Query("SELECT c FROM Contenido c WHERE c.id.usuarioCreador = :cuenta ORDER BY c.id.fechaHoraCreacion DESC")
    List<Contenido> findUserPosts(@org.springframework.data.repository.query.Param("cuenta") String cuenta);
}

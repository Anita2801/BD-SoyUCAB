package com.soyucab.back.repository;

import com.soyucab.back.model.GrupoParticipa;
import com.soyucab.back.model.GrupoParticipaId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface GrupoParticipaRepository extends JpaRepository<GrupoParticipa, GrupoParticipaId> {
    List<GrupoParticipa> findByGrupo_Nombre(String nombreGrupo);

    List<GrupoParticipa> findByUsuario_Cuenta(String cuenta);

    void deleteByGrupo_Nombre(String nombreGrupo);
}

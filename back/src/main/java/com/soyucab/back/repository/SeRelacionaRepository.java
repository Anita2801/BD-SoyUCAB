package com.soyucab.back.repository;

import com.soyucab.back.model.SeRelaciona;
import com.soyucab.back.model.SeRelacionaId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SeRelacionaRepository extends JpaRepository<SeRelaciona, SeRelacionaId> {
    List<SeRelaciona> findByReceptor_Cuenta(String cuenta);

    List<SeRelaciona> findBySolicitante_Cuenta(String cuenta);

    long countByReceptor_CuentaAndEstadoOrSolicitante_CuentaAndEstado(String cuenta1, String estado1, String cuenta2,
            String estado2);
}

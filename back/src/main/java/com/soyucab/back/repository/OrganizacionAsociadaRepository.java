package com.soyucab.back.repository;

import com.soyucab.back.model.OrganizacionAsociada;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OrganizacionAsociadaRepository extends JpaRepository<OrganizacionAsociada, String> {
    java.util.Optional<OrganizacionAsociada> findByUsuario_Cuenta(String cuenta);
}

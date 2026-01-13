package com.soyucab.back.repository;

import com.soyucab.back.model.Persona;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PersonaRepository extends JpaRepository<Persona, String> {
    java.util.Optional<Persona> findByUsuario_Cuenta(String cuenta);

    // Naive suggestion: Find top 3 users that are NOT the current user
    java.util.List<Persona> findTop3ByUsuario_CuentaNot(String cuenta);
}

package com.soyucab.back.repository;

import com.soyucab.back.model.Persona;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PersonaRepository extends JpaRepository<Persona, String> {
    java.util.Optional<Persona> findByUsuario_Cuenta(String cuenta);

    // Naive suggestion: Find top 3 users that are NOT the current user
    java.util.List<Persona> findTop3ByUsuario_CuentaNot(String cuenta);

    @org.springframework.data.jpa.repository.Query("SELECT p FROM Persona p WHERE " +
            "LOWER(p.primerNombre) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
            "LOWER(p.primerApellido) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
            "LOWER(p.usuario.cuenta) LIKE LOWER(CONCAT('%', :query, '%'))")
    java.util.List<Persona> searchPersonas(@org.springframework.web.bind.annotation.RequestParam("query") String query);
}

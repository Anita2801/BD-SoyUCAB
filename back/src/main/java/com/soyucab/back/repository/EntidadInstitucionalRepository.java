package com.soyucab.back.repository;

import com.soyucab.back.model.EntidadInstitucional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EntidadInstitucionalRepository extends JpaRepository<EntidadInstitucional, String> {
    java.util.Optional<EntidadInstitucional> findByNombre(String nombre);
}

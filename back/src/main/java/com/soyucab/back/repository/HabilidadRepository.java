package com.soyucab.back.repository;

import com.soyucab.back.model.Habilidad;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface HabilidadRepository extends JpaRepository<Habilidad, String> {
}

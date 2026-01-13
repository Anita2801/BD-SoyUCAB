package com.soyucab.back.repository;

import com.soyucab.back.model.Grupo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GrupoRepository extends JpaRepository<Grupo, String> {


    java.util.List<Grupo> findByNombreContainingIgnoreCase(String nombre);
}

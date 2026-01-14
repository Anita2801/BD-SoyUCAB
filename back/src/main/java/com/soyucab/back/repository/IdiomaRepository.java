package com.soyucab.back.repository;

import com.soyucab.back.model.Idioma;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IdiomaRepository extends JpaRepository<Idioma, String> {
    java.util.Optional<Idioma> findByIdioma(String idioma);

    java.util.Optional<Idioma> findByIdiomaIgnoreCase(String idioma);
}

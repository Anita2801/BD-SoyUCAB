package com.soyucab.back.repository;

import com.soyucab.back.model.Denuncia;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DenunciaRepository extends JpaRepository<Denuncia, Integer> {
}

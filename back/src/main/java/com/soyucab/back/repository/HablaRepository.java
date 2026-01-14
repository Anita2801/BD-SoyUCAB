package com.soyucab.back.repository;

import com.soyucab.back.model.Habla;
import com.soyucab.back.model.HablaId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface HablaRepository extends JpaRepository<Habla, HablaId> {
    List<Habla> findByPersona_Usuario_Cuenta(String cuenta);
}

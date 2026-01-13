package com.soyucab.back.repository;

import com.soyucab.back.model.Lugar;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LugarRepository extends JpaRepository<Lugar, String> {
}

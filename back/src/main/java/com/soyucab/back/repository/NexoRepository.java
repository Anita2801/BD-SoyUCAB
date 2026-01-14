package com.soyucab.back.repository;

import com.soyucab.back.model.Nexo;
import com.soyucab.back.model.NexoId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface NexoRepository extends JpaRepository<Nexo, NexoId> {
}

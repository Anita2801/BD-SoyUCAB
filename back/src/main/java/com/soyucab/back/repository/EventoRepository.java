package com.soyucab.back.repository;

import com.soyucab.back.model.Evento;
import com.soyucab.back.model.EventoId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EventoRepository extends JpaRepository<Evento, EventoId> {
    List<Evento> findByOrganizador_Cuenta(String cuenta);

    @org.springframework.data.jpa.repository.Query("SELECT e FROM Evento e WHERE e.id.fecha >= CURRENT_DATE ORDER BY e.id.fecha ASC")
    List<Evento> findUpcomingEvents(org.springframework.data.domain.Pageable pageable);
}

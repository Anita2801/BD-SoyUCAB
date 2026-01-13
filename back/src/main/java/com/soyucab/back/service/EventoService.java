package com.soyucab.back.service;

import com.soyucab.back.model.Evento;
import com.soyucab.back.repository.EventoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EventoService {

    @Autowired
    private com.soyucab.back.repository.AsistenciaEventoRepository asistenciaEventoRepository;

    @Autowired
    private EventoRepository eventoRepository;

    public Evento save(Evento evento) {
        return eventoRepository.save(evento);
    }

    public List<Evento> getByOrganizador(String cuenta) {
        return eventoRepository.findByOrganizador_Cuenta(cuenta);
    }

    public List<Evento> getAll() {
        return eventoRepository.findAll();
    }

    public List<Evento> getUpcomingEvents() {
        return eventoRepository.findUpcomingEvents(org.springframework.data.domain.PageRequest.of(0, 3));
    }

    @org.springframework.transaction.annotation.Transactional
    public Evento update(com.soyucab.back.model.EventoId oldId, Evento newInfo) {
        if (oldId.equals(newInfo.getId())) {
            return eventoRepository.findById(oldId).map(evento -> {
                evento.setDescripcion(newInfo.getDescripcion());
                return eventoRepository.save(evento);
            }).orElseThrow(() -> new RuntimeException("Evento not found"));
        } else {
            // ID Changed (e.g. Date change) -> Migration
            Evento oldEvent = eventoRepository.findById(oldId)
                .orElseThrow(() -> new RuntimeException("Evento original not found"));
            
            // Capture attendance before deletion
            List<com.soyucab.back.model.AsistenciaEvento> oldAsistencias = oldEvent.getAsistencias();
            if(oldAsistencias != null) oldAsistencias.size(); // Force fetch

            // Delete old event (and its asistencias via custom delete/cascade)
            delete(oldId);

            // Save new event
            Evento newEvent = eventoRepository.save(newInfo);

            // Restore attendance
            if (oldAsistencias != null) {
                for (com.soyucab.back.model.AsistenciaEvento oldA : oldAsistencias) {
                    com.soyucab.back.model.AsistenciaEvento newA = new com.soyucab.back.model.AsistenciaEvento();
                    com.soyucab.back.model.AsistenciaEventoId newAId = new com.soyucab.back.model.AsistenciaEventoId();
                    newAId.setUsuarioAsistente(oldA.getId().getUsuarioAsistente());
                    newAId.setNombreEvento(newEvent.getId().getNombre());
                    newAId.setFechaEvento(newEvent.getId().getFecha());
                    newAId.setUsuarioOrganizador(newEvent.getId().getUsuarioOrganizador());
                    
                    newA.setId(newAId);
                    newA.setEvento(newEvent);
                    newA.setAsistente(oldA.getAsistente());
                    newA.setFechaRegistro(oldA.getFechaRegistro());
                    newA.setConfirmada(oldA.getConfirmada());
                    
                    asistenciaEventoRepository.save(newA);
                }
            }
            return newEvent;
        }
    }

    @org.springframework.transaction.annotation.Transactional
    public void delete(com.soyucab.back.model.EventoId id) {
        // Build composite key deletion for dependencies first
        asistenciaEventoRepository.deleteByEventoId(id.getNombre(), id.getFecha(), id.getUsuarioOrganizador());
        eventoRepository.deleteById(id);
    }

    @org.springframework.transaction.annotation.Transactional
    public boolean toggleAttendance(com.soyucab.back.model.EventoId eventId, String userId) {
        com.soyucab.back.model.AsistenciaEventoId asistenciaId = new com.soyucab.back.model.AsistenciaEventoId();
        asistenciaId.setNombreEvento(eventId.getNombre());
        asistenciaId.setFechaEvento(eventId.getFecha());
        asistenciaId.setUsuarioOrganizador(eventId.getUsuarioOrganizador());
        asistenciaId.setUsuarioAsistente(userId);

        if (asistenciaEventoRepository.existsById(asistenciaId)) {
            asistenciaEventoRepository.deleteById(asistenciaId);
            return false; // Removed
        } else {
            com.soyucab.back.model.AsistenciaEvento asistencia = new com.soyucab.back.model.AsistenciaEvento();
            asistencia.setId(asistenciaId);
            
            // Set relationships (Proxies usually enough for mapsId/insert)
            com.soyucab.back.model.Usuario user = new com.soyucab.back.model.Usuario();
            user.setCuenta(userId);
            asistencia.setAsistente(user);
            
            com.soyucab.back.model.Evento evento = new com.soyucab.back.model.Evento();
            evento.setId(eventId);
            asistencia.setEvento(evento);
            
            asistencia.setFechaRegistro(java.time.LocalDateTime.now());
            asistencia.setConfirmada(true);
            
            asistenciaEventoRepository.save(asistencia);
            return true; // Added
        }
    }

    public java.util.List<String> getParticipants(com.soyucab.back.model.EventoId eventId) {
        return eventoRepository.findById(eventId)
            .map(evento -> evento.getAsistencias().stream()
                .map(a -> a.getId().getUsuarioAsistente())
                .collect(java.util.stream.Collectors.toList()))
            .orElse(java.util.Collections.emptyList());
    }
}

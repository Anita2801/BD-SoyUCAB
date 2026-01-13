package com.soyucab.back.controller;

import com.soyucab.back.dto.EventoDTO;
import com.soyucab.back.mapper.ContentMapper;
import com.soyucab.back.model.Evento;
import com.soyucab.back.service.EventoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.soyucab.back.model.EventoId;
import java.time.LocalDate;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/eventos")
public class EventoController {

    @Autowired
    private EventoService eventoService;

    @Autowired
    private ContentMapper contentMapper;

    @GetMapping
    public List<EventoDTO> getAllEventos() {
        return eventoService.getAll().stream()
                .map(contentMapper::toDTO)
                .collect(Collectors.toList());
    }

    @GetMapping("/upcoming")
    public List<EventoDTO> getUpcomingEventos() {
        return eventoService.getUpcomingEvents().stream()
                .map(contentMapper::toDTO)
                .collect(Collectors.toList());
    }

    @GetMapping("/organizador/{userId}")
    public List<EventoDTO> getByOrganizador(@PathVariable String userId) {
        return eventoService.getByOrganizador(userId).stream()
                .map(contentMapper::toDTO)
                .collect(Collectors.toList());
    }

    @PostMapping
    public EventoDTO createEvento(@RequestBody EventoDTO dto) {
        Evento entity = contentMapper.toEntity(dto);
        return contentMapper.toDTO(eventoService.save(entity));
    }

    @PutMapping
    public EventoDTO updateEvento(@RequestBody EventoDTO dto, 
                                  @RequestParam String oldName, 
                                  @RequestParam String oldDate, 
                                  @RequestParam String oldOrg) {
        EventoId oldId = new EventoId(oldName, LocalDate.parse(oldDate), oldOrg);
        return contentMapper.toDTO(eventoService.update(oldId, contentMapper.toEntity(dto)));
    }

    @DeleteMapping
    public org.springframework.http.ResponseEntity<Void> deleteEvento(@RequestParam String nombre, @RequestParam String fecha, @RequestParam String organizador) {
        EventoId id = new EventoId(nombre, LocalDate.parse(fecha), organizador);
        eventoService.delete(id);
        return org.springframework.http.ResponseEntity.noContent().build();
    }

    @PostMapping("/attend")
    public boolean toggleAttendance(@RequestBody EventoDTO dto, @RequestParam String userId) {
        EventoId id = new EventoId(dto.getNombre(), dto.getFecha(), dto.getUsuarioOrganizador());
        return eventoService.toggleAttendance(id, userId);
    }

    @GetMapping("/participants")
    public java.util.List<String> getEventParticipants(@RequestParam String nombre, 
                                                        @RequestParam String fecha, 
                                                        @RequestParam String organizador) {
        EventoId id = new EventoId(nombre, LocalDate.parse(fecha), organizador);
        return eventoService.getParticipants(id);
    }
}

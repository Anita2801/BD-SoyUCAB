package com.soyucab.back.controller;

import com.soyucab.back.dto.PersonaDTO;
import com.soyucab.back.mapper.PersonaMapper;
import com.soyucab.back.model.Persona;
import com.soyucab.back.service.PersonaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/personas")
public class PersonaController {

    @Autowired
    private PersonaService personaService;

    @Autowired
    private PersonaMapper personaMapper;

    @GetMapping
    public List<PersonaDTO> getAllPersonas() {
        return personaService.findAll().stream()
                .map(personaMapper::toDTO)
                .collect(Collectors.toList());
    }

    @GetMapping("/{id}")
    public ResponseEntity<PersonaDTO> getPersonaById(@PathVariable String id) {
        return personaService.findById(id)
                .map(personaMapper::toDTO)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public Persona createPersona(@RequestBody Persona persona) {
        return personaService.save(persona);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePersona(@PathVariable String id) {
        personaService.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/usuario/{cuenta}")
    public ResponseEntity<PersonaDTO> getPersonaByUsuario(@PathVariable String cuenta) {
        return personaService.findByUsuario(cuenta)
                .map(personaMapper::toDTO)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/suggestions/{cuenta}")
    public List<PersonaDTO> getSuggestions(@PathVariable String cuenta) {
        return personaService.getSuggestions(cuenta).stream()
                .map(personaMapper::toDTO)
                .collect(Collectors.toList());
    }

    @GetMapping("/search")
    public List<PersonaDTO> search(@RequestParam String query) {
        return personaService.search(query).stream()
                .map(personaMapper::toDTO)
                .collect(Collectors.toList());
    }

    @GetMapping("/profile/{cuenta}")
    public ResponseEntity<com.soyucab.back.dto.ProfileDTO> getProfile(@PathVariable String cuenta) {
        try {
            return ResponseEntity.ok(personaService.getProfile(cuenta));
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PutMapping("/profile/{cuenta}")
    public ResponseEntity<com.soyucab.back.dto.ProfileDTO> updateProfile(
            @PathVariable String cuenta,
            @RequestBody com.soyucab.back.dto.ProfileUpdateDTO updateDTO) {
        try {
            return ResponseEntity.ok(personaService.updateProfile(cuenta, updateDTO));
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/usuario/{cuenta}")
    @org.springframework.transaction.annotation.Transactional
    public ResponseEntity<Void> deleteByUsuario(@PathVariable String cuenta) {
        personaService.deleteByUsuario(cuenta);
        return ResponseEntity.noContent().build();
    }
}

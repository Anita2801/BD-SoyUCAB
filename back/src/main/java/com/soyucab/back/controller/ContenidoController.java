package com.soyucab.back.controller;

import com.soyucab.back.dto.ContenidoDTO;
import com.soyucab.back.mapper.ContentMapper;
import com.soyucab.back.model.Contenido;
import com.soyucab.back.service.ContenidoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/contenido")
public class ContenidoController {

    @Autowired
    private ContenidoService contenidoService;

    @Autowired
    private ContentMapper contentMapper;

    @GetMapping
    public List<ContenidoDTO> getAllContenido() {
        return contenidoService.getAll().stream()
                .map(contentMapper::toDTO)
                .collect(Collectors.toList());
    }

    @GetMapping("/usuario/{userId}")
    public List<ContenidoDTO> getByUsuario(@PathVariable String userId) {
        return contenidoService.getByUsuario(userId).stream()
                .map(contentMapper::toDTO)
                .collect(Collectors.toList());
    }

    @PostMapping
    public ContenidoDTO createContenido(@RequestBody ContenidoDTO dto) {
        Contenido entity = contentMapper.toEntity(dto);
        return contentMapper.toDTO(contenidoService.save(entity));
    }

    @PutMapping
    public ContenidoDTO updateContenido(@RequestBody ContenidoDTO dto) {
        return contentMapper.toDTO(contenidoService.update(dto));
    }

    @DeleteMapping
    public void deleteContenido(@RequestParam String usuario, @RequestParam String fecha) {
        contenidoService.delete(usuario, java.time.LocalDateTime.parse(fecha));
    }

    @PostMapping("/react")
    public java.util.Map<String, Object> toggleReaction(
            @RequestParam String userId,
            @RequestParam String contentOwner,
            @RequestParam String contentDate,
            @RequestParam String reactionType) {
        return contenidoService.toggleReaction(userId, contentOwner, contentDate, reactionType);
    }
}

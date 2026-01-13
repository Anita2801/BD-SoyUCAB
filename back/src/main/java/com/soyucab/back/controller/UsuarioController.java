package com.soyucab.back.controller;

import com.soyucab.back.dto.UsuarioDTO;
import com.soyucab.back.mapper.UsuarioMapper;
import com.soyucab.back.model.Usuario;
import com.soyucab.back.service.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/usuarios")
public class UsuarioController {

    @Autowired
    private UsuarioService usuarioService;

    @Autowired
    private UsuarioMapper usuarioMapper;

    @GetMapping
    public List<UsuarioDTO> getAllUsuarios() {
        return usuarioService.findAll().stream()
                .map(usuarioMapper::toDTO)
                .collect(Collectors.toList());
    }

    @GetMapping("/{id}")
    public ResponseEntity<UsuarioDTO> getUsuarioById(@PathVariable String id) {
        return usuarioService.findById(id)
                .map(usuarioMapper::toDTO)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public UsuarioDTO createUsuario(@RequestBody UsuarioDTO usuarioDTO) {
        Usuario usuario = usuarioMapper.toEntity(usuarioDTO);
        return usuarioMapper.toDTO(usuarioService.save(usuario));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUsuario(@PathVariable String id) {
        usuarioService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}

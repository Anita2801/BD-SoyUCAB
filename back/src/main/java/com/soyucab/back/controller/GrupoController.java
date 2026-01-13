package com.soyucab.back.controller;

import com.soyucab.back.dto.GrupoDTO;
import com.soyucab.back.dto.GrupoParticipaDTO;
import com.soyucab.back.mapper.GroupMapper;
import com.soyucab.back.model.Grupo;
import com.soyucab.back.model.GrupoParticipa;
import com.soyucab.back.service.GrupoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/grupos")
public class GrupoController {

    @Autowired
    private GrupoService grupoService;

    @Autowired
    private GroupMapper groupMapper;

    @GetMapping
    public List<GrupoDTO> getAllGrupos() {
        return grupoService.findAll().stream()
                .map(grupo -> {
                    GrupoDTO dto = groupMapper.toDTO(grupo);
                    // Buscar el creador (rol = "Fundador" o "Creador")
                    grupoService.getMembers(grupo.getNombre()).stream()
                            .filter(m -> "Fundador".equals(m.getRol()) || "Creador".equals(m.getRol()))
                            .findFirst()
                            .ifPresent(creador -> dto.setCreadorNombre(creador.getUsuario().getCuenta()));
                    return dto;
                })
                .collect(Collectors.toList());
    }

    @GetMapping("/featured")
    public List<GrupoDTO> getFeaturedGrupos() {
        return grupoService.getFeaturedGroups().stream()
                .map(groupMapper::toDTO)
                .collect(Collectors.toList());
    }

    @PostMapping
    public GrupoDTO createGrupo(@RequestBody GrupoDTO dto) {
        Grupo entity = groupMapper.toEntity(dto);
        return groupMapper.toDTO(grupoService.save(entity));
    }

    @GetMapping("/{nombre}/miembros")
    public List<GrupoParticipaDTO> getMiembros(@PathVariable String nombre) {
        return grupoService.getMembers(nombre).stream()
                .map(groupMapper::toDTO)
                .collect(Collectors.toList());
    }

    @PostMapping("/join")
    public GrupoParticipaDTO joinGrupo(@RequestBody GrupoParticipaDTO dto) {
        GrupoParticipa entity = groupMapper.toEntity(dto);
        return groupMapper.toDTO(grupoService.addMember(entity));
    }

    @GetMapping("/search")
    public List<GrupoDTO> searchGroups(@RequestParam String query) {
        return grupoService.searchGroups(query).stream()
                .map(groupMapper::toDTO)
                .collect(Collectors.toList());
    }

    @GetMapping("/my-groups/{cuenta}")
    public List<GrupoDTO> getUserGroups(@PathVariable String cuenta) {
        return grupoService.getUserGroups(cuenta).stream()
                .map(gp -> {
                    GrupoDTO dto = groupMapper.toDTO(gp.getGrupo());
                    dto.setMyRole(gp.getRol());
                    return dto;
                })
                .collect(Collectors.toList());
    }

    @PutMapping("/{nombre}")
    public GrupoDTO updateGrupo(@PathVariable String nombre, @RequestBody GrupoDTO dto) {
        Grupo entity = groupMapper.toEntity(dto);
        return groupMapper.toDTO(grupoService.update(nombre, entity));
    }

    @DeleteMapping("/{nombre}")
    public org.springframework.http.ResponseEntity<Void> deleteGrupo(@PathVariable String nombre) {
        grupoService.delete(nombre);
        return org.springframework.http.ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{nombre}/miembros/{usuario}")
    public org.springframework.http.ResponseEntity<Void> leaveGroup(@PathVariable String nombre,
            @PathVariable String usuario) {
        grupoService.removeMember(nombre, usuario);
        return org.springframework.http.ResponseEntity.noContent().build();
    }

    @PutMapping("/{nombre}/miembros/{usuario}/role")
    public org.springframework.http.ResponseEntity<Void> updateMemberRole(
            @PathVariable String nombre,
            @PathVariable String usuario,
            @RequestBody java.util.Map<String, String> body) {
        String newRole = body.get("rol");
        grupoService.updateMemberRole(nombre, usuario, newRole);
        return org.springframework.http.ResponseEntity.ok().build();
    }
}

package com.soyucab.back.mapper;

import com.soyucab.back.dto.GrupoDTO;
import com.soyucab.back.dto.GrupoParticipaDTO;
import com.soyucab.back.model.Grupo;
import com.soyucab.back.model.GrupoParticipa;
import com.soyucab.back.model.GrupoParticipaId;
import com.soyucab.back.model.Usuario;
import org.springframework.stereotype.Component;

@Component
public class GroupMapper {

    public GrupoDTO toDTO(Grupo entity) {
        if (entity == null)
            return null;
        GrupoDTO dto = new GrupoDTO();
        dto.setNombre(entity.getNombre());
        dto.setDescripcion(entity.getDescripcion());
        dto.setEsPrivado("Privado".equalsIgnoreCase(entity.getTipo()));
        dto.setNumeroMiembros(entity.getMiembros() != null ? (long) entity.getMiembros().size() : 0L);
        return dto;
    }

    public Grupo toEntity(GrupoDTO dto) {
        if (dto == null)
            return null;
        Grupo entity = new Grupo();
        entity.setNombre(dto.getNombre());
        entity.setDescripcion(dto.getDescripcion());
        entity.setTipo(Boolean.TRUE.equals(dto.getEsPrivado()) ? "Privado" : "Público");
        return entity;
    }

    public GrupoParticipaDTO toDTO(GrupoParticipa entity) {
        if (entity == null)
            return null;
        GrupoParticipaDTO dto = new GrupoParticipaDTO();
        if (entity.getId() != null) {
            dto.setNombreGrupo(entity.getId().getNombreGrupo());
            dto.setUsuarioMiembro(entity.getId().getUsuarioMiembro());
        }
        dto.setRol(entity.getRol());
        return dto;
    }

    public GrupoParticipa toEntity(GrupoParticipaDTO dto) {
        if (dto == null)
            return null;
        GrupoParticipa entity = new GrupoParticipa();
        GrupoParticipaId id = new GrupoParticipaId();
        id.setNombreGrupo(dto.getNombreGrupo());
        id.setUsuarioMiembro(dto.getUsuarioMiembro());
        entity.setId(id);

        Grupo grupo = new Grupo();
        grupo.setNombre(dto.getNombreGrupo());
        entity.setGrupo(grupo);

        Usuario user = new Usuario();
        user.setCuenta(dto.getUsuarioMiembro());
        entity.setUsuario(user);

        entity.setRol(dto.getRol());
        return entity;
    }
}

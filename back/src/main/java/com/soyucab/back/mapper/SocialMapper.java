package com.soyucab.back.mapper;

import com.soyucab.back.dto.SeRelacionaDTO;
import com.soyucab.back.dto.NotificacionesDTO;
import com.soyucab.back.model.SeRelaciona;
import com.soyucab.back.model.Notificaciones;
import com.soyucab.back.model.Usuario;
import com.soyucab.back.model.SeRelacionaId;
import org.springframework.stereotype.Component;

@Component
public class SocialMapper {

    public SeRelacionaDTO toDTO(SeRelaciona entity) {
        if (entity == null)
            return null;
        SeRelacionaDTO dto = new SeRelacionaDTO();
        if (entity.getId() != null) {
            dto.setUsuarioReceptor(entity.getId().getUsuarioReceptor());
            dto.setUsuarioSolicitante(entity.getId().getUsuarioSolicitante());
        }
        dto.setEstado(entity.getEstado());
        dto.setTipoRelacion(entity.getTipoRelacion());
        dto.setFechaRelacion(entity.getFechaRelacion());
        return dto;
    }

    public SeRelaciona toEntity(SeRelacionaDTO dto) {
        if (dto == null)
            return null;
        SeRelaciona entity = new SeRelaciona();
        SeRelacionaId id = new SeRelacionaId();
        id.setUsuarioReceptor(dto.getUsuarioReceptor());
        id.setUsuarioSolicitante(dto.getUsuarioSolicitante());
        entity.setId(id);

        Usuario receptor = new Usuario();
        receptor.setCuenta(dto.getUsuarioReceptor());
        entity.setReceptor(receptor);

        Usuario solicitante = new Usuario();
        solicitante.setCuenta(dto.getUsuarioSolicitante());
        entity.setSolicitante(solicitante);

        entity.setEstado(dto.getEstado());
        entity.setTipoRelacion(dto.getTipoRelacion());
        entity.setFechaRelacion(dto.getFechaRelacion());
        return entity;
    }

    public NotificacionesDTO toDTO(Notificaciones entity) {
        if (entity == null)
            return null;
        NotificacionesDTO dto = new NotificacionesDTO();
        dto.setId(entity.getId());
        if (entity.getUsuarioDestino() != null) {
            dto.setUsuarioDestino(entity.getUsuarioDestino().getCuenta());
        }
        dto.setMensaje(entity.getMensaje());
        dto.setTipoAlerta(entity.getTipoAlerta());
        dto.setFechaCreacion(entity.getFechaCreacion());
        return dto;
    }

    public Notificaciones toEntity(NotificacionesDTO dto) {
        if (dto == null)
            return null;
        Notificaciones entity = new Notificaciones();
        entity.setId(dto.getId()); // Usually null for new
        Usuario user = new Usuario();
        user.setCuenta(dto.getUsuarioDestino());
        entity.setUsuarioDestino(user);
        entity.setMensaje(dto.getMensaje());
        entity.setTipoAlerta(dto.getTipoAlerta());
        return entity;
    }
}

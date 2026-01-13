package com.soyucab.back.mapper;

import com.soyucab.back.dto.ContenidoDTO;
import com.soyucab.back.dto.EventoDTO;
import com.soyucab.back.model.Contenido;
import com.soyucab.back.model.Evento;
import com.soyucab.back.model.ContenidoId;
import com.soyucab.back.model.EventoId;
import com.soyucab.back.model.Usuario;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class ContentMapper {

    @org.springframework.beans.factory.annotation.Autowired
    private com.soyucab.back.repository.PersonaRepository personaRepository;

    @org.springframework.beans.factory.annotation.Autowired
    private com.soyucab.back.repository.OrganizacionAsociadaRepository organizacionAsociadaRepository;

    public ContenidoDTO toDTO(Contenido entity) {
        if (entity == null)
            return null;
        ContenidoDTO dto = new ContenidoDTO();
        String creatorAccount = null;
        if (entity.getId() != null) {
            creatorAccount = entity.getId().getUsuarioCreador();
            dto.setUsuarioCreador(creatorAccount);
            dto.setFechaHoraCreacion(entity.getId().getFechaHoraCreacion());
        }
        dto.setCuerpo(entity.getCuerpo());
        dto.setMeGusta(entity.getMeGusta());
        dto.setNoMeGusta(entity.getNoMeGusta());

        // Resolve Author Name
        if (creatorAccount != null) {
            final String account = creatorAccount;
            String fullName = personaRepository.findByUsuario_Cuenta(account).map(p -> {
                StringBuilder sb = new StringBuilder();
                if (p.getPrimerNombre() != null)
                    sb.append(p.getPrimerNombre());
                if (p.getSegundoNombre() != null && !p.getSegundoNombre().trim().isEmpty())
                    sb.append(" ").append(p.getSegundoNombre());
                if (p.getPrimerApellido() != null)
                    sb.append(" ").append(p.getPrimerApellido());
                if (p.getSegundoApellido() != null && !p.getSegundoApellido().trim().isEmpty())
                    sb.append(" ").append(p.getSegundoApellido());
                return sb.toString().trim();
            }).orElseGet(() -> {
                return organizacionAsociadaRepository.findByUsuario_Cuenta(account)
                        .map(org -> org.getNombre())
                        .orElse(account);
            });
            dto.setAuthorName(fullName);
        }

        return dto;
    }

    public Contenido toEntity(ContenidoDTO dto) {
        if (dto == null)
            return null;
        Contenido entity = new Contenido();
        ContenidoId id = new ContenidoId();
        id.setUsuarioCreador(dto.getUsuarioCreador());
        // If not provided, we might set it in service, but let's assume dto has it or
        // we handle it
        id.setFechaHoraCreacion(dto.getFechaHoraCreacion() != null ? dto.getFechaHoraCreacion() : LocalDateTime.now());
        entity.setId(id);

        Usuario user = new Usuario();
        user.setCuenta(dto.getUsuarioCreador());
        entity.setUsuario(user);

        entity.setCuerpo(dto.getCuerpo());
        // Defaults managed in entity or service, but setter is good
        if (dto.getMeGusta() != null)
            entity.setMeGusta(dto.getMeGusta());
        if (dto.getNoMeGusta() != null)
            entity.setNoMeGusta(dto.getNoMeGusta());
        return entity;
    }

    public EventoDTO toDTO(Evento entity) {
        if (entity == null)
            return null;
        EventoDTO dto = new EventoDTO();
        if (entity.getId() != null) {
            dto.setNombre(entity.getId().getNombre());
            dto.setFecha(entity.getId().getFecha());
            dto.setUsuarioOrganizador(entity.getId().getUsuarioOrganizador());
        }
        dto.setDescripcion(entity.getDescripcion());

        // Map derived/default fields
        int attendeeCount = entity.getAsistencias() != null ? entity.getAsistencias().size() : 0;
        dto.setAttendees(attendeeCount);
        dto.setTime("Por definir");
        dto.setLocation("Campus UCAB");
        dto.setCategory("General");

        // Populate attendees list
        if (entity.getAsistencias() != null) {
            dto.setAttendeesList(entity.getAsistencias().stream()
                .map(a -> a.getId().getUsuarioAsistente())
                .collect(java.util.stream.Collectors.toList()));
        } else {
            dto.setAttendeesList(java.util.Collections.emptyList());
        }

        return dto;
    }

    public Evento toEntity(EventoDTO dto) {
        if (dto == null)
            return null;
        Evento entity = new Evento();
        EventoId id = new EventoId();
        id.setNombre(dto.getNombre());
        id.setFecha(dto.getFecha());
        id.setUsuarioOrganizador(dto.getUsuarioOrganizador());
        entity.setId(id);

        Usuario user = new Usuario();
        user.setCuenta(dto.getUsuarioOrganizador());
        entity.setOrganizador(user);

        entity.setDescripcion(dto.getDescripcion());
        return entity;
    }
}

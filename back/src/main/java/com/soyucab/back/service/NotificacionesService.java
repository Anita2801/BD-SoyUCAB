package com.soyucab.back.service;

import com.soyucab.back.model.Notificaciones;
import com.soyucab.back.repository.NotificacionesRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class NotificacionesService {

    @Autowired
    private com.soyucab.back.repository.NotificacionesRepository notificacionesRepository;

    @Autowired
    private com.soyucab.back.repository.ReaccionaRepository reaccionaRepository;

    @Autowired
    private com.soyucab.back.repository.PersonaRepository personaRepository;

    @Autowired
    private com.soyucab.back.repository.OrganizacionAsociadaRepository organizacionAsociadaRepository;

    public List<Notificaciones> getByUsuario(String cuenta) {
        return notificacionesRepository.findByUsuarioDestino_Cuenta(cuenta);
    }

    public List<com.soyucab.back.controller.dto.NotificationDTO> getNotificationsForUser(String account) {
        List<com.soyucab.back.controller.dto.NotificationDTO> notifications = new java.util.ArrayList<>();

        // 1. Get Reactions
        List<com.soyucab.back.model.Reacciona> reactions = reaccionaRepository
                .findByContenido_Usuario_CuentaAndUsuario_CuentaNot(account, account);

        for (com.soyucab.back.model.Reacciona r : reactions) {
            com.soyucab.back.controller.dto.NotificationDTO dto = new com.soyucab.back.controller.dto.NotificationDTO();
            dto.setId("react-" + r.getId().hashCode());

            // Resolve Persona for Full Name
            final String reactorAccount = r.getUsuario().getCuenta();
            String fullName = reactorAccount;
            String initials = "UC";

            var personaOpt = personaRepository.findByUsuario_Cuenta(reactorAccount);
            if (personaOpt.isPresent()) {
                var p = personaOpt.get();
                StringBuilder sb = new StringBuilder();
                if (p.getPrimerNombre() != null)
                    sb.append(p.getPrimerNombre());
                if (p.getSegundoNombre() != null && !p.getSegundoNombre().trim().isEmpty())
                    sb.append(" ").append(p.getSegundoNombre());
                if (p.getPrimerApellido() != null)
                    sb.append(" ").append(p.getPrimerApellido());
                if (p.getSegundoApellido() != null && !p.getSegundoApellido().trim().isEmpty())
                    sb.append(" ").append(p.getSegundoApellido());
                fullName = sb.toString().trim();

                if (p.getPrimerNombre() != null && p.getPrimerApellido() != null) {
                    initials = (p.getPrimerNombre().substring(0, 1) + p.getPrimerApellido().substring(0, 1))
                            .toUpperCase();
                }
            } else {
                // Check Organization
                var orgOpt = organizacionAsociadaRepository.findByUsuario_Cuenta(reactorAccount);
                if (orgOpt.isPresent()) {
                    fullName = orgOpt.get().getNombre();
                    if (fullName.length() >= 2) {
                        initials = fullName.substring(0, 2).toUpperCase();
                    } else {
                        initials = fullName.toUpperCase();
                    }
                } else {
                    if (r.getUsuario().getNombre() != null)
                        fullName = r.getUsuario().getNombre();
                }
            }

            dto.setActorName(fullName);
            dto.setActorAvatar(initials);
            dto.setAction(
                    r.getReaccion().equals("Me Gusta") ? "le gustó tu publicación" : "no le gustó tu publicación");
            dto.setContentPreview(
                    r.getContenido().getCuerpo().length() > 30 ? r.getContenido().getCuerpo().substring(0, 30) + "..."
                            : r.getContenido().getCuerpo());
            dto.setTime(r.getContenido().getId().getFechaHoraCreacion().toLocalDate().toString()); // Using post date as
                                                                                                   // proxy
            dto.setType("reaction");
            dto.setRead(false);
            notifications.add(dto);
        }

        // 2. Get General Notifications (like follows)
        List<Notificaciones> generalNotifs = notificacionesRepository.findByUsuarioDestino_Cuenta(account);
        for (Notificaciones n : generalNotifs) {
            com.soyucab.back.controller.dto.NotificationDTO dto = new com.soyucab.back.controller.dto.NotificationDTO();
            dto.setId("notif-" + n.getId());
            dto.setActorName("Sistema"); // We don't have actor info stored in this simple table
            dto.setActorAvatar("SU"); // System User
            dto.setAction(n.getMensaje());
            dto.setContentPreview("");
            dto.setTime(n.getFechaCreacion() != null ? n.getFechaCreacion().toLocalDate().toString() : "Reciente");
            dto.setType(n.getTipoAlerta());
            dto.setRead(false);
            notifications.add(dto);
        }

        return notifications;
    }

    public Notificaciones save(Notificaciones notificacion) {
        return notificacionesRepository.save(notificacion);
    }
}

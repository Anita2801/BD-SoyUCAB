package com.soyucab.back.controller;

import com.soyucab.back.dto.NotificacionesDTO;
import com.soyucab.back.mapper.SocialMapper;
import com.soyucab.back.model.Notificaciones;
import com.soyucab.back.service.NotificacionesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/notificaciones")
public class NotificacionesController {

    @Autowired
    private NotificacionesService notificacionesService;

    @Autowired
    private SocialMapper socialMapper;

    @GetMapping("/{userId}")
    public List<NotificacionesDTO> getUserNotifications(@PathVariable String userId) {
        return notificacionesService.getByUsuario(userId).stream()
                .map(socialMapper::toDTO)
                .collect(Collectors.toList());
    }

    @GetMapping("/reacciones/{userId}")
    public List<com.soyucab.back.controller.dto.NotificationDTO> getReactionNotifications(@PathVariable String userId) {
        return notificacionesService.getNotificationsForUser(userId);
    }

    @PostMapping
    public NotificacionesDTO createNotification(@RequestBody NotificacionesDTO dto) {
        Notificaciones entity = socialMapper.toEntity(dto);
        return socialMapper.toDTO(notificacionesService.save(entity));
    }
}

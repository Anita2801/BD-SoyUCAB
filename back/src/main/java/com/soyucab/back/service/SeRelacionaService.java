package com.soyucab.back.service;

import com.soyucab.back.model.SeRelaciona;
import com.soyucab.back.model.SeRelacionaId;
import com.soyucab.back.repository.SeRelacionaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SeRelacionaService {

    @Autowired
    private SeRelacionaRepository seRelacionaRepository;

    @Autowired
    private com.soyucab.back.repository.NotificacionesRepository notificacionesRepository;

    @org.springframework.transaction.annotation.Transactional
    public SeRelaciona save(SeRelaciona relacion) {
        SeRelaciona saved = seRelacionaRepository.save(relacion);

        // Check if it's a follow (Seguimiento) to send notification
        if ("Seguimiento".equalsIgnoreCase(relacion.getTipoRelacion())) {
            try {
                System.out.println("Processing notification for follow: " + relacion.getSolicitante().getCuenta()
                        + " -> " + relacion.getReceptor().getCuenta());
                com.soyucab.back.model.Notificaciones notif = new com.soyucab.back.model.Notificaciones();
                notif.setUsuarioDestino(relacion.getReceptor());

                String follower = relacion.getSolicitante().getCuenta();
                notif.setMensaje("El usuario @" + follower + " ha comenzado a seguirte.");
                notif.setTipoAlerta("Seguimiento");

                notificacionesRepository.save(notif);
                System.out.println("Notification saved successfully.");
            } catch (Exception e) {
                System.err.println("Error creating notification for follow: " + e.getMessage());
                e.printStackTrace();
            }
        }

        return saved;
    }

    public List<SeRelaciona> getFollowers(String userId) {
        // Assuming "Seguimiento" and userId is the Receptor
        return seRelacionaRepository.findByReceptor_Cuenta(userId);
    }

    public List<SeRelaciona> getFollowing(String userId) {
        // Assuming "Seguimiento" and userId is the Solicitante
        return seRelacionaRepository.findBySolicitante_Cuenta(userId);
    }
}

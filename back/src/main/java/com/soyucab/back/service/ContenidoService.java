package com.soyucab.back.service;

import com.soyucab.back.model.Contenido;
import com.soyucab.back.model.ContenidoId;
import com.soyucab.back.model.Reacciona;
import com.soyucab.back.model.ReaccionaId;
import com.soyucab.back.dto.ContenidoDTO;
import com.soyucab.back.repository.ContenidoRepository;
import com.soyucab.back.repository.ReaccionaRepository;
import com.soyucab.back.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class ContenidoService {

    @Autowired
    private ContenidoRepository contenidoRepository;

    @Autowired
    private ReaccionaRepository reaccionaRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    public Contenido save(Contenido contenido) {
        return contenidoRepository.save(contenido);
    }

    public List<Contenido> getByUsuario(String cuenta) {
        return contenidoRepository.findUserPosts(cuenta);
    }

    public List<Contenido> getAll() {
        return contenidoRepository.findAll();
    }

    public Contenido update(ContenidoDTO dto) {
        ContenidoId id = new ContenidoId(dto.getUsuarioCreador(), dto.getFechaHoraCreacion());
        return contenidoRepository.findById(id).map(existing -> {
            existing.setCuerpo(dto.getCuerpo());
            return contenidoRepository.save(existing);
        }).orElseThrow(() -> new RuntimeException("Contenido no encontrado"));
    }

    public void delete(String usuario, java.time.LocalDateTime fecha) {
        ContenidoId id = new ContenidoId(usuario, fecha);
        contenidoRepository.deleteById(id);
    }

    @Transactional
    public Map<String, Object> toggleReaction(String userId, String contentOwner, String contentDate,
            String reactionType) {
        LocalDateTime fecha = LocalDateTime.parse(contentDate);
        ReaccionaId reaccionaId = new ReaccionaId(userId, contentOwner, fecha);

        Optional<Reacciona> existingReaction = reaccionaRepository.findById(reaccionaId);
        Map<String, Object> response = new HashMap<>();

        if (existingReaction.isPresent()) {
            Reacciona existing = existingReaction.get();
            if (existing.getReaccion().equals(reactionType)) {
                // Same reaction type - remove it (toggle off)
                reaccionaRepository.delete(existing);
                response.put("action", "removed");
                response.put("reactionType", reactionType);
            } else {
                // Different reaction type - update it
                existing.setReaccion(reactionType);
                reaccionaRepository.save(existing);
                response.put("action", "updated");
                response.put("oldType", existing.getReaccion());
                response.put("reactionType", reactionType);
            }
        } else {
            // No existing reaction - create new one
            Reacciona newReaction = new Reacciona();
            newReaction.setId(reaccionaId);
            newReaction.setUsuario(usuarioRepository.findById(userId).orElse(null));

            ContenidoId contenidoId = new ContenidoId(contentOwner, fecha);
            newReaction.setContenido(contenidoRepository.findById(contenidoId).orElse(null));
            newReaction.setReaccion(reactionType);

            reaccionaRepository.save(newReaction);
            response.put("action", "added");
            response.put("reactionType", reactionType);
        }

        return response;
    }
}

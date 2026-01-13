package com.soyucab.back.repository;

import com.soyucab.back.model.ChatMiembro;
import com.soyucab.back.model.ChatMiembroId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ChatMiembroRepository extends JpaRepository<ChatMiembro, ChatMiembroId> {
    List<ChatMiembro> findByUsuarioChat(String usuarioChat);

    List<ChatMiembro> findByChat(com.soyucab.back.model.Chat chat);

    List<ChatMiembro> findByChatParticipaAndFechaChat(String chatParticipa, java.time.LocalDateTime fechaChat);

    void deleteByChatParticipaAndFechaChat(String chatParticipa, java.time.LocalDateTime fechaChat);
}

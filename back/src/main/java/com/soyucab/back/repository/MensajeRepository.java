package com.soyucab.back.repository;

import com.soyucab.back.model.Mensaje;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface MensajeRepository extends JpaRepository<Mensaje, Long> {
    List<Mensaje> findByChatNombreChatAndChatFechaCreacionChatOrderByFechaAsc(String nombreChat,
            LocalDateTime fechaCreacionChat);

    void deleteByChatNombreChatAndChatFechaCreacionChat(String nombreChat, LocalDateTime fechaCreacionChat);
}

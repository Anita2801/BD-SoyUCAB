package com.soyucab.back.repository;

import com.soyucab.back.model.Mensaje;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface MensajeRepository extends JpaRepository<Mensaje, Long> {

    @Query("SELECT m FROM Mensaje m WHERE m.chat.nombreChat = :nombreChat AND m.chat.fechaCreacionChat = :fechaCreacionChat ORDER BY m.fecha ASC")
    List<Mensaje> findByChatNombreChatAndChatFechaCreacionChatOrderByFechaAsc(@Param("nombreChat") String nombreChat,
            @Param("fechaCreacionChat") LocalDateTime fechaCreacionChat);

    @Modifying
    @Query("DELETE FROM Mensaje m WHERE m.chat.nombreChat = :nombreChat AND m.chat.fechaCreacionChat = :fechaCreacionChat")
    void deleteByChatNombreChatAndChatFechaCreacionChat(@Param("nombreChat") String nombreChat,
            @Param("fechaCreacionChat") LocalDateTime fechaCreacionChat);

    @Modifying
    @Query("DELETE FROM Mensaje m WHERE m.sender.cuenta = :cuenta")
    void deleteBySenderCuenta(@Param("cuenta") String cuenta);
}

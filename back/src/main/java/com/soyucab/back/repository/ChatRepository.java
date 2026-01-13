package com.soyucab.back.repository;

import com.soyucab.back.model.Chat;
import com.soyucab.back.model.ChatId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ChatRepository extends JpaRepository<Chat, ChatId> {
}

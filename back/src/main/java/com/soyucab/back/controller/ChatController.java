package com.soyucab.back.controller;

import com.soyucab.back.controller.dto.ChatDTO;
import com.soyucab.back.service.ChatService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/chats")
@CrossOrigin(origins = "http://localhost:4200")
public class ChatController {

    @Autowired
    private ChatService chatService;

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<ChatDTO>> getUserChats(@PathVariable String userId) {
        return ResponseEntity.ok(chatService.getUserChats(userId));
    }

    @GetMapping("/{chatName}/{chatDate}/messages")
    public ResponseEntity<List<ChatDTO.MessageDTO>> getChatMessages(
            @PathVariable String chatName,
            @PathVariable String chatDate,
            @RequestParam String userId) {
        return ResponseEntity.ok(chatService.getChatMessages(chatName, chatDate, userId));
    }

    @PostMapping("/send")
    public ResponseEntity<Void> sendMessage(@RequestBody Map<String, String> payload) {
        chatService.sendMessage(
                payload.get("chatName"),
                payload.get("chatDate"),
                payload.get("senderId"),
                payload.get("content"));
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{chatName}/{chatDate}/members")
    public List<com.soyucab.back.controller.dto.ChatMemberDTO> getChatMembers(@PathVariable String chatName,
            @PathVariable String chatDate) {
        return chatService.getChatMembers(chatName, chatDate);
    }

    // ====================== CRUD ENDPOINTS ======================

    @PostMapping
    public ResponseEntity<ChatDTO> createChat(@RequestBody Map<String, Object> payload) {
        String chatName = (String) payload.get("chatName");
        String creatorId = (String) payload.get("creatorId");
        @SuppressWarnings("unchecked")
        List<String> memberIds = (List<String>) payload.get("memberIds");

        ChatDTO chat = chatService.createChat(chatName, creatorId, memberIds);
        return ResponseEntity.ok(chat);
    }

    @DeleteMapping("/{chatName}/{chatDate}")
    public ResponseEntity<Void> deleteChat(
            @PathVariable String chatName,
            @PathVariable String chatDate,
            @RequestParam String userId) {
        chatService.deleteChat(chatName, chatDate, userId);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{chatName}/{chatDate}/leave/{userId}")
    public ResponseEntity<Void> leaveChat(
            @PathVariable String chatName,
            @PathVariable String chatDate,
            @PathVariable String userId) {
        chatService.leaveChat(chatName, chatDate, userId);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/{chatName}/{chatDate}/members/{targetUserId}/role")
    public ResponseEntity<Void> updateMemberRole(
            @PathVariable String chatName,
            @PathVariable String chatDate,
            @PathVariable String targetUserId,
            @RequestBody Map<String, String> payload) {
        String newRole = payload.get("role");
        String requesterId = payload.get("requesterId");
        chatService.updateMemberRole(chatName, chatDate, targetUserId, newRole, requesterId);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{chatName}/{chatDate}/role/{userId}")
    public ResponseEntity<Map<String, String>> getUserRole(
            @PathVariable String chatName,
            @PathVariable String chatDate,
            @PathVariable String userId) {
        String role = chatService.getUserRoleInChat(chatName, chatDate, userId);
        return ResponseEntity.ok(Map.of("role", role != null ? role : ""));
    }

    @GetMapping("/user/{userId}/friends")
    public ResponseEntity<List<Map<String, String>>> getFriends(@PathVariable String userId) {
        return ResponseEntity.ok(chatService.getFriends(userId));
    }

    @PostMapping("/{chatName}/{chatDate}/members")
    public ResponseEntity<Void> addMembers(
            @PathVariable String chatName,
            @PathVariable String chatDate,
            @RequestBody Map<String, Object> payload) {
        @SuppressWarnings("unchecked")
        List<String> memberIds = (List<String>) payload.get("memberIds");
        String requesterId = (String) payload.get("requesterId");

        chatService.addMembersToChat(chatName, chatDate, memberIds, requesterId);
        return ResponseEntity.ok().build();
    }
}

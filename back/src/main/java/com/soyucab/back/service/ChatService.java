package com.soyucab.back.service;

import com.soyucab.back.controller.dto.ChatDTO;
import com.soyucab.back.model.*;
import com.soyucab.back.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ChatService {

    @Autowired
    private ChatMiembroRepository chatMiembroRepository;

    @Autowired
    private ChatRepository chatRepository;

    @Autowired
    private MensajeRepository mensajeRepository;

    @Autowired

    private UsuarioRepository usuarioRepository;

    @Autowired
    private com.soyucab.back.repository.PersonaRepository personaRepository;

    @Autowired
    private com.soyucab.back.repository.OrganizacionAsociadaRepository organizacionAsociadaRepository;

    @Autowired
    private SeRelacionaRepository seRelacionaRepository;

    @Transactional
    public List<ChatDTO> getUserChats(String userId) {
        System.out.println("DEBUG: getUserChats for " + userId);
        List<ChatMiembro> memberships = chatMiembroRepository.findByUsuarioChat(userId);
        System.out.println("DEBUG: Found " + memberships.size() + " memberships");
        List<ChatDTO> chatDTOs = new ArrayList<>();

        for (ChatMiembro member : memberships) {
            try {
                Chat chat = member.getChat();
                if (chat == null) {
                    System.out.println("DEBUG: Relation failed for member " + member.getChatParticipa()
                            + ". Trying manual lookup...");
                    System.out.println("DEBUG: Member Date: " + member.getFechaChat());
                    ChatId cid = new ChatId(member.getChatParticipa(), member.getFechaChat());
                    chat = chatRepository.findById(cid).orElse(null);
                    if (chat != null)
                        System.out.println("DEBUG: Manual lookup SUCCESS!");
                    else
                        System.out.println("DEBUG: Manual lookup FAILED for " + cid.getNombreChat() + " "
                                + cid.getFechaCreacionChat());
                }

                if (chat != null) {
                    ChatDTO dto = new ChatDTO(chat.getNombreChat(), chat.getFechaCreacionChat());

                    // Fetch messages to get last message
                    List<Mensaje> messages = mensajeRepository
                            .findByChatNombreChatAndChatFechaCreacionChatOrderByFechaAsc(
                                    chat.getNombreChat(), chat.getFechaCreacionChat());

                    if (!messages.isEmpty()) {
                        Mensaje last = messages.get(messages.size() - 1);
                        dto.setLastMessage(last.getContenido());
                        dto.setLastMessageTime(last.getFecha());
                    } else {
                        dto.setLastMessage("Inicio del chat");
                        dto.setLastMessageTime(chat.getFechaCreacionChat());
                    }

                    chatDTOs.add(dto);
                } else {
                    System.out.println("DEBUG: Chat is PERMANENTLY null for member " + member.getChatParticipa());
                }
            } catch (Exception e) {
                System.out.println("DEBUG: Error processing chat for member: " + e.getMessage());
                e.printStackTrace();
            }
        }
        System.out.println("DEBUG: Returning " + chatDTOs.size() + " chats");
        return chatDTOs;
    }

    public List<ChatDTO.MessageDTO> getChatMessages(String chatName, String chatDateStr, String currentUserId) {
        String decodedName = chatName;
        try {
            decodedName = URLDecoder.decode(chatName, StandardCharsets.UTF_8.name());
        } catch (Exception e) {
        }

        // Simple parse assuming ISO format
        LocalDateTime chatDate = LocalDateTime.parse(chatDateStr);

        List<Mensaje> messages = mensajeRepository.findByChatNombreChatAndChatFechaCreacionChatOrderByFechaAsc(
                decodedName,
                chatDate);
        return messages.stream().map(m -> {
            String name = resolveFullName(m.getSender());
            return new ChatDTO.MessageDTO(
                    m.getSender().getCuenta(),
                    name,
                    m.getContenido(),
                    m.getFecha(),
                    m.getSender().getCuenta().equals(currentUserId));
        }).collect(Collectors.toList());
    }

    public void sendMessage(String chatName, String chatDateStr, String senderId, String content) {
        LocalDateTime chatDate = LocalDateTime.parse(chatDateStr);
        ChatId chatId = new ChatId(chatName, chatDate);
        Chat chat = chatRepository.findById(chatId).orElseThrow(() -> new RuntimeException("Chat not found"));
        Usuario sender = usuarioRepository.findById(senderId).orElseThrow(() -> new RuntimeException("User not found"));

        Mensaje msg = new Mensaje();
        msg.setChat(chat);
        msg.setSender(sender);
        msg.setContenido(content);
        msg.setFecha(LocalDateTime.now());
        mensajeRepository.save(msg);
    }

    @Transactional
    public List<com.soyucab.back.controller.dto.ChatMemberDTO> getChatMembers(String chatName, String chatDateStr) {
        try {
            System.out.println("DEBUG: Requesting members for chat: " + chatName + " date: " + chatDateStr);
            String decodedName = java.net.URLDecoder.decode(chatName, java.nio.charset.StandardCharsets.UTF_8);
            System.out.println("DEBUG: Decoded Name: " + decodedName);

            LocalDateTime chatDate = LocalDateTime.parse(chatDateStr);
            ChatId chatId = new ChatId(decodedName, chatDate);

            Chat chat = chatRepository.findById(chatId).orElseThrow(() -> {
                System.out.println("DEBUG: Chat not found with ID: " + chatId);
                return new RuntimeException("Chat not found");
            });

            List<ChatMiembro> members = chatMiembroRepository.findByChatParticipaAndFechaChat(decodedName, chatDate);
            System.out.println("DEBUG: Found " + members.size() + " members via direct query.");

            // Name resolution via repositories

            return members.stream().map(m -> {
                try {
                    Usuario u = m.getUsuario();
                    if (u == null) {
                        return new com.soyucab.back.controller.dto.ChatMemberDTO(m.getUsuarioChat(), "Unknown",
                                m.getRolChat(), "");
                    }
                    String fullName = resolveFullName(u);

                    return new com.soyucab.back.controller.dto.ChatMemberDTO(u.getCuenta(), fullName.trim(),
                            m.getRolChat(), u.getEmail());
                } catch (Exception e) {
                    System.out.println("DEBUG: Error mapping member: " + e.getMessage());
                    e.printStackTrace();
                    return null;
                }
            }).filter(java.util.Objects::nonNull).collect(Collectors.toList());
        } catch (Exception e) {
            System.out.println("DEBUG: Global error in getChatMembers: " + e.getMessage());
            e.printStackTrace();
            throw e;
        }
    }

    private String resolveFullName(Usuario u) {
        if (u == null)
            return "Unknown";

        String fullName = "";
        java.util.Optional<com.soyucab.back.model.Persona> personaOpt = personaRepository
                .findByUsuario_Cuenta(u.getCuenta());

        if (personaOpt.isPresent()) {
            com.soyucab.back.model.Persona p = personaOpt.get();
            fullName = (p.getPrimerNombre() != null ? p.getPrimerNombre() : "") + " " +
                    (p.getSegundoNombre() != null ? p.getSegundoNombre() + " " : "") +
                    (p.getPrimerApellido() != null ? p.getPrimerApellido() : "") + " " +
                    (p.getSegundoApellido() != null ? p.getSegundoApellido() : "");
            fullName = fullName.trim().replaceAll(" +", " ");
        } else {
            java.util.Optional<com.soyucab.back.model.OrganizacionAsociada> orgOpt = organizacionAsociadaRepository
                    .findByUsuario_Cuenta(u.getCuenta());
            if (orgOpt.isPresent()) {
                fullName = orgOpt.get().getNombre();
            } else {
                fullName = (u.getNombre() != null ? u.getNombre() : "") + " "
                        + (u.getApellido() != null ? u.getApellido() : "");
                if (fullName.trim().isEmpty()) {
                    fullName = u.getCuenta();
                }
            }
        }
        return fullName;
    }

    // ====================== CRUD METHODS ======================

    @Transactional
    public ChatDTO createChat(String chatName, String creatorId, List<String> memberIds) {
        // Create chat
        Chat chat = new Chat();
        chat.setNombreChat(chatName);
        chat.setFechaCreacionChat(LocalDateTime.now());
        chatRepository.save(chat);

        // Add creator as member with role "Creador"
        Usuario creator = usuarioRepository.findById(creatorId)
                .orElseThrow(() -> new RuntimeException("Creator not found"));

        ChatMiembro creatorMember = new ChatMiembro();
        creatorMember.setChatParticipa(chatName);
        creatorMember.setFechaChat(chat.getFechaCreacionChat());
        creatorMember.setUsuarioChat(creatorId);
        creatorMember.setRolChat("Creador");
        creatorMember.setChat(chat);
        creatorMember.setUsuario(creator);
        chatMiembroRepository.save(creatorMember);

        // Add other members
        if (memberIds != null) {
            for (String memberId : memberIds) {
                if (!memberId.equals(creatorId)) {
                    addMemberToChat(chatName, chat.getFechaCreacionChat(), memberId, "Miembro");
                }
            }
        }

        ChatDTO dto = new ChatDTO(chat.getNombreChat(), chat.getFechaCreacionChat());
        dto.setLastMessage("Chat creado");
        dto.setLastMessageTime(chat.getFechaCreacionChat());
        return dto;
    }

    @Transactional
    public void addMemberToChat(String chatName, LocalDateTime chatDate, String userId, String role) {
        Usuario user = usuarioRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        Chat chat = chatRepository.findById(new ChatId(chatName, chatDate))
                .orElseThrow(() -> new RuntimeException("Chat not found"));

        ChatMiembro member = new ChatMiembro();
        member.setChatParticipa(chatName);
        member.setFechaChat(chatDate);
        member.setUsuarioChat(userId);
        member.setRolChat(role);
        member.setChat(chat);
        member.setUsuario(user);
        chatMiembroRepository.save(member);
    }

    @Transactional
    public void deleteChat(String chatName, String chatDateStr, String userId) {
        String decodedName = decode(chatName);
        LocalDateTime chatDate = LocalDateTime.parse(chatDateStr);

        // Verify user is creator
        ChatMiembroId memberId = new ChatMiembroId(decodedName, chatDate, userId);
        ChatMiembro member = chatMiembroRepository.findById(memberId)
                .orElseThrow(() -> new RuntimeException("User not a member"));

        if (!"Creador".equals(member.getRolChat())) {
            throw new RuntimeException("Solo el creador puede eliminar el chat");
        }

        // Delete all messages
        mensajeRepository.deleteByChatNombreChatAndChatFechaCreacionChat(decodedName, chatDate);

        // Delete all members
        chatMiembroRepository.deleteByChatParticipaAndFechaChat(decodedName, chatDate);

        // Delete chat
        chatRepository.deleteById(new ChatId(decodedName, chatDate));
    }

    @Transactional
    public void leaveChat(String chatName, String chatDateStr, String userId) {
        String decodedName = decode(chatName);
        LocalDateTime chatDate = LocalDateTime.parse(chatDateStr);
        ChatMiembroId memberId = new ChatMiembroId(decodedName, chatDate, userId);

        ChatMiembro member = chatMiembroRepository.findById(memberId)
                .orElseThrow(() -> new RuntimeException("User not a member"));

        // If creator leaves, delete the chat
        if ("Creador".equals(member.getRolChat())) {
            deleteChat(chatName, chatDateStr, userId);
        } else {
            chatMiembroRepository.deleteById(memberId);
        }
    }

    @Transactional
    public void updateMemberRole(String chatName, String chatDateStr, String targetUserId, String newRole,
            String requesterId) {
        String decodedName = decode(chatName);
        LocalDateTime chatDate = LocalDateTime.parse(chatDateStr);

        // Verify requester is creator
        ChatMiembroId requesterMemberId = new ChatMiembroId(decodedName, chatDate, requesterId);
        ChatMiembro requester = chatMiembroRepository.findById(requesterMemberId)
                .orElseThrow(() -> new RuntimeException("Requester not a member"));

        if (!"Creador".equals(requester.getRolChat())) {
            throw new RuntimeException("Solo el creador puede cambiar roles");
        }

        // Update target user role
        ChatMiembroId targetMemberId = new ChatMiembroId(decodedName, chatDate, targetUserId);
        ChatMiembro target = chatMiembroRepository.findById(targetMemberId)
                .orElseThrow(() -> new RuntimeException("Target user not a member"));

        if ("Creador".equals(target.getRolChat())) {
            throw new RuntimeException("No se puede cambiar el rol del creador");
        }

        target.setRolChat(newRole);
        chatMiembroRepository.save(target);
    }

    public String getUserRoleInChat(String chatName, String chatDateStr, String userId) {
        String decodedName = decode(chatName);
        LocalDateTime chatDate = LocalDateTime.parse(chatDateStr);
        ChatMiembroId memberId = new ChatMiembroId(decodedName, chatDate, userId);
        return chatMiembroRepository.findById(memberId)
                .map(ChatMiembro::getRolChat)
                .orElse(null);
    }

    public List<Map<String, String>> getFriends(String userId) {
        List<SeRelaciona> asReceptor = seRelacionaRepository.findByReceptor_Cuenta(userId);
        List<SeRelaciona> asSolicitante = seRelacionaRepository.findBySolicitante_Cuenta(userId);

        List<Usuario> addableUsers = new ArrayList<>();

        // For relationships where userId is the RECEPTOR (someone sent them a request)
        for (SeRelaciona rel : asReceptor) {
            // For Amistad: only if accepted, both can add each other
            if ("Aceptada".equalsIgnoreCase(rel.getEstado()) && "Amistad".equalsIgnoreCase(rel.getTipoRelacion())) {
                addableUsers.add(rel.getSolicitante());
            }
            // For Seguimiento where userId is receptor: the solicitante follows userId
            // But userId cannot add them based on this relationship - skip
        }

        // For relationships where userId is the SOLICITANTE (they initiated)
        for (SeRelaciona rel : asSolicitante) {
            // For Amistad: only if accepted
            if ("Aceptada".equalsIgnoreCase(rel.getEstado()) && "Amistad".equalsIgnoreCase(rel.getTipoRelacion())) {
                addableUsers.add(rel.getReceptor());
            }
            // For Seguimiento: userId follows receptor - no acceptance needed for follows!
            // If the record exists, userId is following receptor and can add them
            if ("Seguimiento".equalsIgnoreCase(rel.getTipoRelacion())) {
                addableUsers.add(rel.getReceptor());
            }
        }

        System.out.println("DEBUG getFriends for " + userId + ": found " + addableUsers.size() + " addable users");

        return addableUsers.stream().distinct().map(u -> {
            String fullName = resolveFullName(u);
            Map<String, String> m = new HashMap<>();
            m.put("username", u.getCuenta());
            m.put("fullName", fullName.trim());
            return m;
        }).collect(Collectors.toList());
    }

    @Transactional
    public void addMembersToChat(String chatName, String chatDateStr, List<String> memberIds, String requesterId) {
        String decodedName = decode(chatName);
        LocalDateTime chatDate = LocalDateTime.parse(chatDateStr);

        ChatMiembroId reqId = new ChatMiembroId(decodedName, chatDate, requesterId);
        ChatMiembro req = chatMiembroRepository.findById(reqId)
                .orElseThrow(() -> new RuntimeException("Requester not in chat"));

        if (!"Creador".equals(req.getRolChat()) && !"Administrador".equals(req.getRolChat())) {
            throw new RuntimeException("Only Admin/Creator can add members");
        }

        for (String mid : memberIds) {
            if (!chatMiembroRepository.existsById(new ChatMiembroId(decodedName, chatDate, mid))) {
                addMemberToChat(decodedName, chatDate, mid, "Miembro");
            }
        }
    }

    private String decode(String s) {
        try {
            return URLDecoder.decode(s, StandardCharsets.UTF_8.name());
        } catch (Exception e) {
            return s;
        }
    }
}

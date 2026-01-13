package com.soyucab.back.controller.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class NotificationDTO {
    private String id; // random or composite
    private String actorName; // Who reacted
    private String actorAvatar; // Initials or URL
    private String action; // "reaccionó a tu publicación"
    private String contentPreview; // snippet of post
    private String time; // "Hace un momento" (since we lack timestamp, we'll mock or use post date)
    private String type; // "reaction", "comment", etc.
    private boolean read;
}

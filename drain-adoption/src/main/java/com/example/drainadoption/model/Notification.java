package com.example.drainadoption.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Table(name = "notifications")
@Data
@NoArgsConstructor
public class Notification {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private NotificationType type;
    
    @Column(nullable = false)
    private String message;
    
    @Column(nullable = false)
    private Long drainId;
    
    private Long userId; // The user who triggered the notification (adopter or commenter)
    
    @Column(nullable = false)
    private Boolean read = false;
    
    @Column(nullable = false)
    private LocalDateTime createdAt;
    
    public enum NotificationType {
        DRAIN_ADOPTED,
        COMMENT_ADDED
    }
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        if (read == null) {
            read = false;
        }
    }
}

package com.example.drainadoption.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.example.drainadoption.exception.DrainNotFoundException;
import com.example.drainadoption.exception.AdoptionConflictException;
import com.example.drainadoption.model.Drain;
import com.example.drainadoption.model.User;
import com.example.drainadoption.model.Comment;
import com.example.drainadoption.model.Notification;
import com.example.drainadoption.model.Notification.NotificationType;
import com.example.drainadoption.repository.DrainRepository;
import com.example.drainadoption.repository.UserRepository;
import com.example.drainadoption.repository.CommentRepository;
import com.example.drainadoption.repository.NotificationRepository;
import com.example.drainadoption.dto.DrainDTO;
import com.example.drainadoption.dto.DrainUpdateDTO;
import com.example.drainadoption.dto.CommentRequest;
import com.example.drainadoption.dto.CommentDTO;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/drains")
public class DrainController {

    @Autowired
    private DrainRepository drainRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private NotificationRepository notificationRepository;

    @GetMapping
    public ResponseEntity<List<DrainDTO>> getAllDrains() {
        List<DrainDTO> drainDTOs = drainRepository.findAll().stream()
                .map(DrainDTO::fromEntity)
                .collect(Collectors.toList());
        return ResponseEntity.ok(drainDTOs);
    }

    @GetMapping("/{id}")
    public ResponseEntity<DrainDTO> getDrain(@PathVariable Long id) {
        return drainRepository.findById(id)
                .map(drain -> ResponseEntity.ok(DrainDTO.fromEntity(drain)))
                .orElseThrow(() -> new DrainNotFoundException(id));
    }

    @PostMapping
    public ResponseEntity<DrainDTO> createDrain(@RequestBody DrainDTO drainDTO) {
        Drain drain = new Drain();
        drain.setName(drainDTO.getName());
        drain.setImageUrl(drainDTO.getImageUrl());
        drain.setLatitude(drainDTO.getLatitude());
        drain.setLongitude(drainDTO.getLongitude());
        
        Drain savedDrain = drainRepository.save(drain);
        return ResponseEntity.status(HttpStatus.CREATED).body(DrainDTO.fromEntity(savedDrain));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteDrain(@PathVariable Long id) {
        Drain drain = drainRepository.findById(id)
                .orElseThrow(() -> new DrainNotFoundException(id));
        
        // 1. Delete all comments associated with this drain
        commentRepository.deleteByDrainId(id);
        
        // 2. Delete all notifications associated with this drain
        notificationRepository.deleteByDrainId(id);
        
        // 3. Clear the adoption relationship if drain is adopted
        if (drain.getAdoptedByUser() != null) {
            User adopter = drain.getAdoptedByUser();
            adopter.setAdoptedDrain(null);
            userRepository.save(adopter);
        }
        
        // 4. Now safe to delete the drain
        drainRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/adopt")
    public ResponseEntity<?> adoptDrain(@PathVariable Long id, @RequestParam Long userId) {
        // 1. Find user by userId
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new AdoptionConflictException("User not found with ID: " + userId));

        // 2. Check if user already adopted a different drain
        if (user.getAdoptedDrain() != null) {
            throw new AdoptionConflictException("User has already adopted drain with ID: " + 
                user.getAdoptedDrain().getId());
        }

        // 3. Find the drain by id
        Drain drain = drainRepository.findById(id)
                .orElseThrow(() -> new DrainNotFoundException(id));

        // 4. Check if drain already adopted by someone else
        if (drain.getAdoptedByUser() != null) {
            // Check if the adopting user still exists
            Long adopterId = drain.getAdoptedByUser().getId();
            if (!userRepository.existsById(adopterId)) {
                // Orphaned relationship - clean it up
                drain.setAdoptedByUser(null);
                drainRepository.save(drain);
            } else {
                throw new AdoptionConflictException("Drain is already adopted by user with ID: " + adopterId);
            }
        }

        // 5. Set relationships both ways
        user.setAdoptedDrain(drain);
        drain.setAdoptedByUser(user);

        // 6. Save both entities
        userRepository.save(user);
        drainRepository.save(drain);

        // 7. Create notification for admins
        Notification notification = new Notification();
        notification.setType(NotificationType.DRAIN_ADOPTED);
        notification.setMessage(user.getName() + " adopted drain: " + drain.getName());
        notification.setDrainId(drain.getId());
        notification.setUserId(user.getId());
        notificationRepository.save(notification);

        return ResponseEntity.ok()
                .body(DrainDTO.fromEntity(drain));
    }

    @PutMapping("/{id}")
    public ResponseEntity<DrainDTO> updateDrain(
            @PathVariable Long id,
            @RequestBody DrainUpdateDTO updateDTO) {
        
        Drain drain = drainRepository.findById(id)
                .orElseThrow(() -> new DrainNotFoundException(id));

        // Update drain fields
        if (updateDTO.getName() != null) {
            drain.setName(updateDTO.getName());
        }
        if (updateDTO.getImageUrl() != null) {
            drain.setImageUrl(updateDTO.getImageUrl());
        }
        if (updateDTO.getLatitude() != null) {
            drain.setLatitude(updateDTO.getLatitude());
        }
        if (updateDTO.getLongitude() != null) {
            drain.setLongitude(updateDTO.getLongitude());
        }

        Drain updatedDrain = drainRepository.save(drain);
        return ResponseEntity.ok(DrainDTO.fromEntity(updatedDrain));
    }

    // Comment endpoints
    @PostMapping("/{id}/comments")
    public ResponseEntity<CommentDTO> addComment(
            @PathVariable Long id,
            @RequestParam Long userId,
            @RequestBody CommentRequest commentRequest) {
        
        // Verify drain exists
        Drain drain = drainRepository.findById(id)
                .orElseThrow(() -> new DrainNotFoundException(id));

        // Verify user exists
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with ID: " + userId));

        // Check if drain is adopted
        if (drain.getAdoptedByUser() == null) {
            return ResponseEntity.badRequest().build();
        }

        // Only the adopter of this drain can comment
        if (!drain.getAdoptedByUser().getId().equals(userId)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        // Create comment
        Comment comment = new Comment();
        comment.setDrainId(id);
        comment.setUserId(userId);
        comment.setText(commentRequest.getText());
        comment.setImageUrl(commentRequest.getImageUrl());
        
        Comment savedComment = commentRepository.save(comment);

        // Create notification for admins
        Notification notification = new Notification();
        notification.setType(NotificationType.COMMENT_ADDED);
        notification.setMessage(user.getName() + " commented on drain: " + drain.getName());
        notification.setDrainId(drain.getId());
        notification.setUserId(user.getId());
        notificationRepository.save(notification);

        // Convert to DTO
        CommentDTO dto = new CommentDTO(
            savedComment.getId(),
            savedComment.getDrainId(),
            savedComment.getUserId(),
            user.getName(),
            savedComment.getText(),
            savedComment.getImageUrl(),
            savedComment.getCreatedAt()
        );

        return ResponseEntity.status(HttpStatus.CREATED).body(dto);
    }

    @GetMapping("/{id}/comments")
    public ResponseEntity<List<CommentDTO>> getComments(@PathVariable Long id) {
        // Verify drain exists
        if (!drainRepository.existsById(id)) {
            throw new DrainNotFoundException(id);
        }

        List<CommentDTO> comments = commentRepository.findByDrainIdOrderByCreatedAtDesc(id)
                .stream()
                .map(comment -> {
                    User user = userRepository.findById(comment.getUserId()).orElse(null);
                    return new CommentDTO(
                        comment.getId(),
                        comment.getDrainId(),
                        comment.getUserId(),
                        user != null ? user.getName() : "Unknown User",
                        comment.getText(),
                        comment.getImageUrl(),
                        comment.getCreatedAt()
                    );
                })
                .collect(Collectors.toList());

        return ResponseEntity.ok(comments);
    }

    @DeleteMapping("/{drainId}/comments/{commentId}")
    public ResponseEntity<Void> deleteComment(
            @PathVariable Long drainId,
            @PathVariable Long commentId) {
        
        // Verify drain exists
        if (!drainRepository.existsById(drainId)) {
            throw new DrainNotFoundException(drainId);
        }

        // Verify comment exists and belongs to this drain
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new RuntimeException("Comment not found with ID: " + commentId));

        if (!comment.getDrainId().equals(drainId)) {
            return ResponseEntity.badRequest().build();
        }

        commentRepository.deleteById(commentId);
        return ResponseEntity.noContent().build();
    }

    // Admin endpoint to reset drain adoption (fix orphaned relationships)
    @PostMapping("/{id}/reset-adoption")
    public ResponseEntity<?> resetDrainAdoption(@PathVariable Long id) {
        Drain drain = drainRepository.findById(id)
                .orElseThrow(() -> new DrainNotFoundException(id));

        // If drain has an adopter, clear the relationship from both sides
        if (drain.getAdoptedByUser() != null) {
            User adopter = drain.getAdoptedByUser();
            adopter.setAdoptedDrain(null);
            userRepository.save(adopter);
            
            drain.setAdoptedByUser(null);
            drainRepository.save(drain);
        }

        return ResponseEntity.ok()
                .body(java.util.Map.of("message", "Drain adoption reset successfully"));
    }
}
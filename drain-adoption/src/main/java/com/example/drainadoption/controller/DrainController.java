package com.example.drainadoption.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.example.drainadoption.exception.DrainNotFoundException;
import com.example.drainadoption.exception.AdoptionConflictException;
import com.example.drainadoption.model.Drain;
import com.example.drainadoption.model.User;
import com.example.drainadoption.repository.DrainRepository;
import com.example.drainadoption.repository.UserRepository;
import com.example.drainadoption.dto.DrainDTO;
import com.example.drainadoption.dto.DrainUpdateDTO;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/drains")
@CrossOrigin(origins = "http://localhost:5173")
public class DrainController {

    @Autowired
    private DrainRepository drainRepository;

    @Autowired
    private UserRepository userRepository;

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
            throw new AdoptionConflictException("Drain is already adopted by user with ID: " + 
                drain.getAdoptedByUser().getId());
        }

        // 5. Set relationships both ways
        user.setAdoptedDrain(drain);
        drain.setAdoptedByUser(user);

        // 6. Save both entities
        userRepository.save(user);
        drainRepository.save(drain);

        return ResponseEntity.ok()
                .body(DrainDTO.fromEntity(drain));
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateDrain(
            @PathVariable Long id,
            @RequestParam Long userId,
            @RequestBody DrainUpdateDTO updateDTO) {
        
        Drain drain = drainRepository.findById(id)
                .orElse(null);

        if (drain == null) {
            return ResponseEntity.notFound().build();
        }

        // Check if the drain is adopted by the requesting user
        if (drain.getAdoptedByUser() == null || 
            !drain.getAdoptedByUser().getId().equals(userId)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body("You can only update drains you have adopted");
        }

        if (updateDTO.getName() != null) {
            drain.setName(updateDTO.getName());
        }
        if (updateDTO.getImageUrl() != null) {
            drain.setImageUrl(updateDTO.getImageUrl());
        }

        Drain updatedDrain = drainRepository.save(drain);
        return ResponseEntity.ok(DrainDTO.fromEntity(updatedDrain));
    }
}
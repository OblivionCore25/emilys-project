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
        if (!drainRepository.existsById(id)) {
            throw new DrainNotFoundException(id);
        }
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
}
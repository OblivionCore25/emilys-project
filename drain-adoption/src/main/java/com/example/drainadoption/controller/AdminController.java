package com.example.drainadoption.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import com.example.drainadoption.dto.RegisterRequest;
import com.example.drainadoption.model.User;
import com.example.drainadoption.model.User.UserRole;
import com.example.drainadoption.repository.UserRepository;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/admin")
@CrossOrigin(origins = {"http://localhost:3000", "http://localhost:5173"})
public class AdminController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    /**
     * Create the first admin user. This endpoint is only accessible when no admin users exist.
     * After the first admin is created, this endpoint will be disabled.
     * 
     * Security Note: This endpoint should be used immediately after deployment.
     * Consider disabling it after initial setup by setting a configuration flag.
     */
    @PostMapping("/create-first-admin")
    public ResponseEntity<?> createFirstAdmin(@RequestBody RegisterRequest request) {
        // Check if any admin user already exists
        boolean adminExists = userRepository.findAll().stream()
            .anyMatch(user -> user.getRole() == UserRole.ADMIN);

        if (adminExists) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Admin user already exists. Use the promotion endpoint instead.");
            error.put("message", "For security reasons, this endpoint only works when no admin users exist.");
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(error);
        }

        // Check if email already exists
        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Email already exists");
            return ResponseEntity.badRequest().body(error);
        }

        // Create the first admin user
        User admin = new User();
        admin.setName(request.getName());
        admin.setEmail(request.getEmail());
        admin.setPassword(passwordEncoder.encode(request.getPassword()));
        admin.setRole(UserRole.ADMIN);

        User savedAdmin = userRepository.save(admin);

        Map<String, Object> response = new HashMap<>();
        response.put("message", "First admin user created successfully");
        response.put("userId", savedAdmin.getId());
        response.put("email", savedAdmin.getEmail());
        response.put("name", savedAdmin.getName());
        response.put("role", savedAdmin.getRole().name());

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * Promote an existing user to admin role.
     * This endpoint requires ADMIN authentication.
     * 
     * @param userId The ID of the user to promote
     */
    @PutMapping("/promote/{userId}")
    public ResponseEntity<?> promoteToAdmin(@PathVariable Long userId) {
        Optional<User> userOptional = userRepository.findById(userId);

        if (userOptional.isEmpty()) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "User not found");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
        }

        User user = userOptional.get();

        if (user.getRole() == UserRole.ADMIN) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "User is already an admin");
            return ResponseEntity.badRequest().body(error);
        }

        // Promote user to admin
        user.setRole(UserRole.ADMIN);
        User updatedUser = userRepository.save(user);

        Map<String, Object> response = new HashMap<>();
        response.put("message", "User promoted to admin successfully");
        response.put("userId", updatedUser.getId());
        response.put("email", updatedUser.getEmail());
        response.put("name", updatedUser.getName());
        response.put("role", updatedUser.getRole().name());

        return ResponseEntity.ok(response);
    }
}

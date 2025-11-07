package com.example.drainadoption.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import com.example.drainadoption.dto.LoginRequest;
import com.example.drainadoption.dto.LoginResponse;
import com.example.drainadoption.dto.RegisterRequest;
import com.example.drainadoption.model.User;
import com.example.drainadoption.model.User.UserRole;
import com.example.drainadoption.repository.UserRepository;
import com.example.drainadoption.security.JwtUtil;

import java.util.Optional;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = {"http://localhost:3000", "http://localhost:5173"})
public class AuthController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterRequest request) {
        // Check if email already exists
        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            return ResponseEntity.badRequest().body("Email already exists");
        }

        // Create new user
        User user = new User();
        user.setName(request.getName());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        
        // Public registration always creates ADOPTER users
        // Admin users must be created through secure admin endpoints
        user.setRole(UserRole.ADOPTER);

        User savedUser = userRepository.save(user);

        // Generate token
        String token = jwtUtil.generateToken(
            savedUser.getEmail(),
            savedUser.getId(),
            savedUser.getRole().name()
        );

        return ResponseEntity.status(HttpStatus.CREATED).body(
            new LoginResponse(
                token,
                savedUser.getId(),
                savedUser.getEmail(),
                savedUser.getName(),
                savedUser.getRole().name()
            )
        );
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {
        Optional<User> userOptional = userRepository.findByEmail(request.getEmail());

        if (userOptional.isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body("Invalid email or password");
        }

        User user = userOptional.get();

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body("Invalid email or password");
        }

        // Generate token
        String token = jwtUtil.generateToken(
            user.getEmail(),
            user.getId(),
            user.getRole().name()
        );

        return ResponseEntity.ok(
            new LoginResponse(
                token,
                user.getId(),
                user.getEmail(),
                user.getName(),
                user.getRole().name()
            )
        );
    }
}

package com.eventhub.backend.auth.service;

import com.eventhub.backend.auth.dto.request.LoginRequest;
import com.eventhub.backend.auth.dto.request.RegisterRequest;
import com.eventhub.backend.auth.dto.response.AuthResponse;
import com.eventhub.backend.auth.entity.Role;
import com.eventhub.backend.auth.entity.User;
import com.eventhub.backend.auth.repository.UserRepository;
import com.eventhub.backend.auth.security.JwtUtil;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final UserRepository repo;
    private final PasswordEncoder encoder;
    private final JwtUtil jwtUtil;

    public AuthService(UserRepository repo,
                       PasswordEncoder encoder,
                       JwtUtil jwtUtil) {
        this.repo = repo;
        this.encoder = encoder;
        this.jwtUtil = jwtUtil;
    }

    public void register(RegisterRequest req) {

        if (repo.findByEmail(req.getEmail()).isPresent()) {
            throw new RuntimeException("Email already exists");
        }

        User user = new User();
        user.setName(req.getName());
        user.setEmail(req.getEmail());
        user.setPassword(encoder.encode(req.getPassword()));
        user.setRole(Role.USER);

        repo.save(user);
    }

    public AuthResponse login(LoginRequest req) {

        User user = repo.findByEmail(req.getEmail())
                .orElseThrow(() -> new RuntimeException("Invalid credentials"));

        if (!encoder.matches(req.getPassword(), user.getPassword())) {
            throw new RuntimeException("Invalid credentials");
        }

        String token = jwtUtil.generateToken(
                user.getEmail(),
                user.getRole().name()
        );

        return new AuthResponse(token, user.getRole().name());
    }
}
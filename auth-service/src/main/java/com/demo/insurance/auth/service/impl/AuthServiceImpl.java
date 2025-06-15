package com.demo.insurance.auth.service.impl;

import com.demo.insurance.auth.dto.*;
import com.demo.insurance.auth.entity.User;
import com.demo.insurance.auth.repository.UserRepository;
import com.demo.insurance.auth.service.AuthService;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.security.authentication.*;
import org.springframework.security.core.userdetails.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.util.Date;

@Service
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepo;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authManager;
    private final String jwtSecret = "your-32-byte-or-longer-secret-key-here";

    public AuthServiceImpl(UserRepository userRepo,
                           PasswordEncoder passwordEncoder,
                           AuthenticationManager authManager) {
        this.userRepo = userRepo;
        this.passwordEncoder = passwordEncoder;
        this.authManager = authManager;
    }

    @Override
    public void register(UserDto dto) {
        if (userRepo.findByUsername(dto.getUsername()).isPresent()) {
            throw new RuntimeException("Username already taken");
        }
        User user = new User();
        user.setUsername(dto.getUsername());
        user.setPassword(passwordEncoder.encode(dto.getPassword()));
        user.setRole(dto.getRole());
        userRepo.save(user);
    }

    @Override
    public LoginResponse authenticate(LoginRequest req) {
        // Kimlik doğrulama
        authManager.authenticate(
                new UsernamePasswordAuthenticationToken(req.getUsername(), req.getPassword())
        );

        // JWT oluşturma
        String token = Jwts.builder()
                .setSubject(req.getUsername())
                .claim("role", userRepo.findByUsername(req.getUsername()).get().getRole())
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + 86400000)) // 24h
                .signWith(Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8)))
                .compact();

        return new LoginResponse(token, "Login successful");
    }
}

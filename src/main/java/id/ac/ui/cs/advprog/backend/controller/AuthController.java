package id.ac.ui.cs.advprog.backend.controller;

import id.ac.ui.cs.advprog.backend.model.User;
import id.ac.ui.cs.advprog.backend.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class AuthController {

    private final UserService userService;

    public AuthController(UserService userService) {
        this.userService = userService;
    }

    // DTOs menggunakan Java Record
    public record RegisterRequest(String username, String password, String fullName) {}
    public record LoginRequest(String username, String password) {}
    public record ProfileUpdateRequest(String fullName, String bio) {}

    @PostMapping("/auth/register")
    public ResponseEntity<?> register(@RequestBody RegisterRequest request) {
        try {
            User user = userService.registerUser(request.username(), request.password(), request.fullName());
            return ResponseEntity.ok("Registrasi berhasil untuk: " + user.getUsername());
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/auth/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {
        try {
            User user = userService.loginUser(request.username(), request.password());
            return ResponseEntity.ok("Login berhasil! Selamat datang " + user.getFullName());
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/profile/{username}")
    public ResponseEntity<?> getProfile(@PathVariable String username) {
        try {
            User user = userService.getProfile(username);
            return ResponseEntity.ok(user);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PutMapping("/profile/{username}")
    public ResponseEntity<?> updateProfile(@PathVariable String username, @RequestBody ProfileUpdateRequest request) {
        try {
            User user = userService.updateProfile(username, request.fullName(), request.bio());
            return ResponseEntity.ok(user);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
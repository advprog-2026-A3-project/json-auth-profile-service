package id.ac.ui.cs.advprog.authnprofile.controller;

import id.ac.ui.cs.advprog.authnprofile.dto.*;
import id.ac.ui.cs.advprog.authnprofile.model.User;
import id.ac.ui.cs.advprog.authnprofile.service.ProfileService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class ProfileController {

    private final ProfileService profileService;

    // Public: view any profile by username
    @GetMapping("/profile/public/{username}")
    public ResponseEntity<UserProfileResponse> getPublicProfile(@PathVariable String username) {
        return ResponseEntity.ok(profileService.getPublicProfile(username));
    }

    // Authenticated: view own profile
    @GetMapping("/profile/me")
    public ResponseEntity<UserProfileResponse> getMyProfile(@AuthenticationPrincipal User user) {
        return ResponseEntity.ok(profileService.getProfile(user.getId()));
    }

    // Authenticated: update own profile
    @PutMapping("/profile/me")
    public ResponseEntity<UserProfileResponse> updateProfile(
            @AuthenticationPrincipal User user,
            @RequestBody ProfileUpdateRequest request) {
        return ResponseEntity.ok(profileService.updateProfile(user.getId(), request));
    }

    // Authenticated: submit KYC
    @PostMapping("/profile/kyc")
    public ResponseEntity<UserProfileResponse> submitKyc(
            @AuthenticationPrincipal User user,
            @Valid @RequestBody KycRequest request) {
        return ResponseEntity.ok(profileService.submitKyc(user.getId(), request));
    }

    // Admin: list all users
    @GetMapping("/admin/users")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<UserProfileResponse>> getAllUsers() {
        return ResponseEntity.ok(profileService.getAllUsers());
    }

    // Admin: approve KYC
    @PostMapping("/admin/users/{userId}/kyc/approve")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UserProfileResponse> approveKyc(@PathVariable Long userId) {
        return ResponseEntity.ok(profileService.approveKyc(userId));
    }

    // Admin: reject KYC
    @PostMapping("/admin/users/{userId}/kyc/reject")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UserProfileResponse> rejectKyc(@PathVariable Long userId) {
        return ResponseEntity.ok(profileService.rejectKyc(userId));
    }

    // Admin: ban/unban user
    @PatchMapping("/admin/users/{userId}/status")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UserProfileResponse> setUserStatus(
            @PathVariable Long userId,
            @RequestParam boolean active) {
        return ResponseEntity.ok(profileService.setUserActive(userId, active));
    }
}
package id.ac.ui.cs.advprog.authnprofile.controller;

import id.ac.ui.cs.advprog.authnprofile.dto.*;
import id.ac.ui.cs.advprog.authnprofile.model.Role;
import id.ac.ui.cs.advprog.authnprofile.model.User;
import id.ac.ui.cs.advprog.authnprofile.service.ProfileService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.PageRequest;
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

    // Public: view any profile by user ID
    @GetMapping("/profile/{userId}")
    public ResponseEntity<UserProfileResponse> getProfileById(@PathVariable Long userId) {
        return ResponseEntity.ok(profileService.getProfile(userId));
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
            @Valid @RequestBody KycSubmissionRequest request) {
        return ResponseEntity.ok(profileService.submitKyc(user.getId(), request));
    }

    // Admin: list all users with pagination and filtering
    @GetMapping("/admin/users")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<PaginatedResponse<UserProfileResponse>> getAllUsers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(required = false) Boolean active) {
        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok(profileService.getAllUsers(pageable, active));
    }

    // Admin: validate KYC (approve or reject)
    @PostMapping("/admin/kyc/{userId}/validate")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UserProfileResponse> validateKyc(
            @PathVariable Long userId,
            @Valid @RequestBody KycValidationRequest request) {
        return ResponseEntity.ok(profileService.validateKyc(userId, request));
    }

    // Admin: approve KYC (legacy endpoint, kept for backward compatibility)
    @PostMapping("/admin/users/{userId}/kyc/approve")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UserProfileResponse> approveKyc(@PathVariable Long userId) {
        return ResponseEntity.ok(profileService.approveKyc(userId));
    }

    // Admin: reject KYC (legacy endpoint, kept for backward compatibility)
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

    // Admin: update user role
    @PatchMapping("/admin/users/{userId}/role")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UserProfileResponse> updateUserRole(
            @PathVariable Long userId,
            @Valid @RequestBody RoleUpdateRequest request) {
        Role role;
        try {
            role = Role.valueOf(request.getRole().toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid role: " + request.getRole()
                    + ". Must be one of: TITIPERS, JASTIPER, ADMIN");
        }
        return ResponseEntity.ok(profileService.updateUserRole(userId, role));
    }
}
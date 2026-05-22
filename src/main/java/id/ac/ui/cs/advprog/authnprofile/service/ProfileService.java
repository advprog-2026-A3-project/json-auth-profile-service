package id.ac.ui.cs.advprog.authnprofile.service;

import id.ac.ui.cs.advprog.authnprofile.dto.*;
import id.ac.ui.cs.advprog.authnprofile.model.Role;
import id.ac.ui.cs.advprog.authnprofile.model.User;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface ProfileService {
    UserProfileResponse getProfile(Long userId);
    UserProfileResponse getPublicProfile(String username);
    UserProfileResponse updateProfile(Long userId, ProfileUpdateRequest request);
    UserProfileResponse submitKyc(Long userId, KycSubmissionRequest request);
    UserProfileResponse validateKyc(Long userId, KycValidationRequest request);
    UserProfileResponse approveKyc(Long targetUserId);
    UserProfileResponse rejectKyc(Long targetUserId);
    List<UserProfileResponse> getAllUsers();
    PaginatedResponse<UserProfileResponse> getAllUsers(Pageable pageable, Boolean active);
    UserProfileResponse setUserActive(Long userId, boolean active);
    UserProfileResponse updateUserRole(Long userId, Role role);
}
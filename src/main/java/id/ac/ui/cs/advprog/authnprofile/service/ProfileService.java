package id.ac.ui.cs.advprog.authnprofile.service;

import id.ac.ui.cs.advprog.authnprofile.dto.*;
import id.ac.ui.cs.advprog.authnprofile.model.User;

import java.util.List;

public interface ProfileService {
    UserProfileResponse getProfile(Long userId);
    UserProfileResponse getPublicProfile(String username);
    UserProfileResponse updateProfile(Long userId, ProfileUpdateRequest request);
    UserProfileResponse submitKyc(Long userId, KycRequest request);
    UserProfileResponse approveKyc(Long targetUserId);
    UserProfileResponse rejectKyc(Long targetUserId);
    List<UserProfileResponse> getAllUsers();
    UserProfileResponse setUserActive(Long userId, boolean active);
}
package id.ac.ui.cs.advprog.authnprofile.service;

import id.ac.ui.cs.advprog.authnprofile.dto.*;
import id.ac.ui.cs.advprog.authnprofile.model.KycStatus;
import id.ac.ui.cs.advprog.authnprofile.model.Role;
import id.ac.ui.cs.advprog.authnprofile.model.User;
import id.ac.ui.cs.advprog.authnprofile.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProfileServiceImpl implements ProfileService {

    private final UserRepository userRepository;

    @Override
    public UserProfileResponse getProfile(Long userId) {
        User user = findUserById(userId);
        return UserProfileResponse.from(user);
    }

    @Override
    public UserProfileResponse getPublicProfile(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + username));
        return UserProfileResponse.from(user);
    }

    @Override
    @Transactional
    public UserProfileResponse updateProfile(Long userId, ProfileUpdateRequest request) {
        User user = findUserById(userId);

        if (request.getUsername() != null && !request.getUsername().isBlank()) {
            if (!request.getUsername().equals(user.getDisplayUsername())
                    && userRepository.existsByUsername(request.getUsername())) {
                throw new IllegalArgumentException("Username already taken");
            }
            user.setUsername(request.getUsername());
        }
        if (request.getFullName() != null) user.setFullName(request.getFullName());
        if (request.getBio() != null) user.setBio(request.getBio());

        return UserProfileResponse.from(userRepository.save(user));
    }

    @Override
    @Transactional
    public UserProfileResponse submitKyc(Long userId, KycRequest request) {
        User user = findUserById(userId);

        if (user.getKycStatus() == KycStatus.PENDING) {
            throw new IllegalStateException("KYC already pending review");
        }
        if (user.getRole() == Role.JASTIPER) {
            throw new IllegalStateException("Already a Jastiper");
        }

        user.setFullName(request.getFullName());
        if (request.getSocialMediaLink() != null) {
            user.setSocialMediaLink(request.getSocialMediaLink());
        }
        user.setKycStatus(KycStatus.PENDING);

        return UserProfileResponse.from(userRepository.save(user));
    }

    @Override
    @Transactional
    public UserProfileResponse approveKyc(Long targetUserId) {
        User user = findUserById(targetUserId);

        if (user.getKycStatus() != KycStatus.PENDING) {
            throw new IllegalStateException("No pending KYC for this user");
        }

        user.setKycStatus(KycStatus.APPROVED);
        user.setRole(Role.JASTIPER);

        return UserProfileResponse.from(userRepository.save(user));
    }

    @Override
    @Transactional
    public UserProfileResponse rejectKyc(Long targetUserId) {
        User user = findUserById(targetUserId);

        if (user.getKycStatus() != KycStatus.PENDING) {
            throw new IllegalStateException("No pending KYC for this user");
        }

        user.setKycStatus(KycStatus.REJECTED);
        return UserProfileResponse.from(userRepository.save(user));
    }

    @Override
    public List<UserProfileResponse> getAllUsers() {
        return userRepository.findAll().stream()
                .map(UserProfileResponse::from)
                .toList();
    }

    @Override
    @Transactional
    public UserProfileResponse setUserActive(Long userId, boolean active) {
        User user = findUserById(userId);
        user.setActive(active);
        return UserProfileResponse.from(userRepository.save(user));
    }

    private User findUserById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + id));
    }
}
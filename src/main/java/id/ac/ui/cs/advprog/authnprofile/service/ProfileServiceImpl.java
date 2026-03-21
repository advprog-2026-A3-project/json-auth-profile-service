package id.ac.ui.cs.advprog.authnprofile.service;

import id.ac.ui.cs.advprog.authnprofile.dto.*;
import id.ac.ui.cs.advprog.authnprofile.model.KycRequest;
import id.ac.ui.cs.advprog.authnprofile.model.KycStatus;
import id.ac.ui.cs.advprog.authnprofile.model.Role;
import id.ac.ui.cs.advprog.authnprofile.model.User;
import id.ac.ui.cs.advprog.authnprofile.repository.KycRequestRepository;
import id.ac.ui.cs.advprog.authnprofile.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProfileServiceImpl implements ProfileService {

    private final UserRepository userRepository;
    private final KycRequestRepository kycRequestRepository;
    private final UsernameGenerationService usernameGenerationService;

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

        // Handle username: either use provided username or auto-generate if not set
        if (request.getUsername() != null && !request.getUsername().isBlank()) {
            if (!request.getUsername().equals(user.getDisplayUsername())
                    && userRepository.existsByUsername(request.getUsername())) {
                throw new IllegalArgumentException("Username already taken");
            }
            user.setUsername(request.getUsername());
        } else if (user.getDisplayUsername() == null || user.getDisplayUsername().isBlank()) {
            // Auto-generate username if user doesn't have one yet
            String generatedUsername = usernameGenerationService.generateUniqueUsername(user.getEmail());
            user.setUsername(generatedUsername);
        }

        if (request.getDisplayName() != null) user.setDisplayName(request.getDisplayName());
        if (request.getPhoneNumber() != null) user.setPhoneNumber(request.getPhoneNumber());
        if (request.getBio() != null) user.setBio(request.getBio());

        return UserProfileResponse.from(userRepository.save(user));
    }

    @Override
    @Transactional
    public UserProfileResponse submitKyc(Long userId, KycSubmissionRequest request) {
        User user = findUserById(userId);

        // Validation: User cannot submit KYC if already a Jastiper
        if (user.getRole() == Role.JASTIPER) {
            throw new IllegalStateException("Already a Jastiper");
        }

        // Validation: User cannot submit KYC if one is already pending
        if (user.getKycStatus() == KycStatus.PENDING) {
            throw new IllegalStateException("KYC already pending review");
        }

        // Update user information
        user.setDisplayName(request.getDisplayName());
        if (request.getPhoneNumber() != null) {
            user.setPhoneNumber(request.getPhoneNumber());
        }
        if (request.getSocialMediaLink() != null) {
            user.setSocialMediaLink(request.getSocialMediaLink());
        }
        user.setKycStatus(KycStatus.PENDING);

        // Save updated user
        User savedUser = userRepository.save(user);

        // Create KycRequest record for audit trail and admin review
        KycRequest kycRequest = KycRequest.builder()
                .user(savedUser)
                .fullName(request.getDisplayName())
                .socialMediaLink(request.getSocialMediaLink())
                .status(KycStatus.PENDING)
                .build();

        kycRequestRepository.save(kycRequest);

        return UserProfileResponse.from(savedUser);
    }

    @Override
    @Transactional
    public UserProfileResponse validateKyc(Long userId, KycValidationRequest request) {
        User user = findUserById(userId);

        if (user.getKycStatus() != KycStatus.PENDING) {
            throw new IllegalStateException("No pending KYC for this user");
        }

        if (request.isApprove()) {
            user.setKycStatus(KycStatus.APPROVED);
            user.setRole(Role.JASTIPER);
        } else if (request.isReject()) {
            user.setKycStatus(KycStatus.REJECTED);
        } else {
            throw new IllegalArgumentException("Invalid action. Must be APPROVE or REJECT");
        }

        User savedUser = userRepository.save(user);

        // Update KycRequest record if exists
        kycRequestRepository.findByUser(user).ifPresent(kycRequest -> {
            kycRequest.setStatus(user.getKycStatus());
            kycRequest.setReviewNotes(request.getReviewNotes());
            kycRequest.setReviewedAt(java.time.LocalDateTime.now());
            kycRequestRepository.save(kycRequest);
        });

        return UserProfileResponse.from(savedUser);
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
    public PaginatedResponse<UserProfileResponse> getAllUsers(Pageable pageable, Boolean active) {
        var usersPage = active != null
                ? userRepository.findAllByActive(active, pageable)
                : userRepository.findAll(pageable);

        return PaginatedResponse.from(
                usersPage.map(UserProfileResponse::from)
        );
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
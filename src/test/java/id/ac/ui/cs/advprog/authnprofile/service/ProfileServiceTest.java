package id.ac.ui.cs.advprog.authnprofile.service;

import id.ac.ui.cs.advprog.authnprofile.dto.*;
import id.ac.ui.cs.advprog.authnprofile.model.*;
import id.ac.ui.cs.advprog.authnprofile.repository.KycRequestRepository;
import id.ac.ui.cs.advprog.authnprofile.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProfileServiceTest {

    @Mock UserRepository userRepository;
    @Mock KycRequestRepository kycRequestRepository;
    @Mock UsernameGenerationService usernameGenerationService;
    @InjectMocks ProfileServiceImpl profileService;

    private User baseUser;

    @BeforeEach
    void setUp() {
        baseUser = User.builder()
                .id(1L).email("user@example.com").username("user")
                .password("hashed").role(Role.TITIPERS)
                .kycStatus(KycStatus.NONE).active(true).build();
    }

    @Test
    void getProfile_success() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(baseUser));
        UserProfileResponse result = profileService.getProfile(1L);
        assertThat(result.getEmail()).isEqualTo("user@example.com");
    }

    @Test
    void getProfile_notFound_throws() {
        when(userRepository.findById(99L)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> profileService.getProfile(99L))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void updateProfile_changesUsernameAndBio() {
        ProfileUpdateRequest req = new ProfileUpdateRequest();
        req.setUsername("newusername");
        req.setBio("Hello world");

        when(userRepository.findById(1L)).thenReturn(Optional.of(baseUser));
        when(userRepository.existsByUsername("newusername")).thenReturn(false);
        when(userRepository.save(any())).thenReturn(baseUser);

        profileService.updateProfile(1L, req);

        verify(userRepository).save(argThat(u -> "newusername".equals(u.getDisplayUsername())));
    }

    @Test
    void updateProfile_duplicateUsername_throws() {
        ProfileUpdateRequest req = new ProfileUpdateRequest();
        req.setUsername("taken");

        when(userRepository.findById(1L)).thenReturn(Optional.of(baseUser));
        when(userRepository.existsByUsername("taken")).thenReturn(true);

        assertThatThrownBy(() -> profileService.updateProfile(1L, req))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Username already taken");
    }

    @Test
    void submitKyc_success() {
        KycSubmissionRequest kycReq = new KycSubmissionRequest();
        kycReq.setDisplayName("User Lengkap");
        kycReq.setPhoneNumber("08123456789");
        kycReq.setSocialMediaLink("https://twitter.com/user");

        when(userRepository.findById(1L)).thenReturn(Optional.of(baseUser));
        when(userRepository.save(any())).thenReturn(baseUser);
        when(kycRequestRepository.save(any())).thenReturn(new KycRequest());

        profileService.submitKyc(1L, kycReq);

        // Verify user status updated
        verify(userRepository).save(argThat(u -> u.getKycStatus() == KycStatus.PENDING));

        // Verify KycRequest record created
        verify(kycRequestRepository).save(argThat(kycReq_ ->
                kycReq_.getFullName().equals("User Lengkap") &&
                kycReq_.getStatus() == KycStatus.PENDING
        ));
    }

    @Test
    void submitKyc_alreadyPending_throws() {
        baseUser.setKycStatus(KycStatus.PENDING);
        when(userRepository.findById(1L)).thenReturn(Optional.of(baseUser));

        KycSubmissionRequest req = new KycSubmissionRequest();
        req.setDisplayName("Name");

        assertThatThrownBy(() -> profileService.submitKyc(1L, req))
                .isInstanceOf(IllegalStateException.class);
    }

    @Test
    void submitKyc_alreadyJastiper_throws() {
        baseUser.setRole(Role.JASTIPER);
        baseUser.setKycStatus(KycStatus.APPROVED);

        when(userRepository.findById(1L)).thenReturn(Optional.of(baseUser));

        KycSubmissionRequest req = new KycSubmissionRequest();
        req.setDisplayName("Name");

        assertThatThrownBy(() -> profileService.submitKyc(1L, req))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("Already a Jastiper");
    }

    @Test
    void validateKyc_approve_success() {
        baseUser.setKycStatus(KycStatus.PENDING);
        KycValidationRequest validationReq = new KycValidationRequest();
        validationReq.setAction("APPROVE");
        validationReq.setReviewNotes("Good documentation");

        when(userRepository.findById(1L)).thenReturn(Optional.of(baseUser));
        when(userRepository.save(any())).thenReturn(baseUser);
        when(kycRequestRepository.findByUser(any())).thenReturn(Optional.of(new KycRequest()));

        profileService.validateKyc(1L, validationReq);

        verify(userRepository).save(argThat(u ->
                u.getRole() == Role.JASTIPER && u.getKycStatus() == KycStatus.APPROVED));
        verify(kycRequestRepository).save(argThat(kycReq ->
                kycReq.getStatus() == KycStatus.APPROVED &&
                "Good documentation".equals(kycReq.getReviewNotes())
        ));
    }

    @Test
    void validateKyc_reject_success() {
        baseUser.setKycStatus(KycStatus.PENDING);
        KycValidationRequest validationReq = new KycValidationRequest();
        validationReq.setAction("REJECT");
        validationReq.setReviewNotes("Documents unclear");

        when(userRepository.findById(1L)).thenReturn(Optional.of(baseUser));
        when(userRepository.save(any())).thenReturn(baseUser);
        when(kycRequestRepository.findByUser(any())).thenReturn(Optional.of(new KycRequest()));

        profileService.validateKyc(1L, validationReq);

        verify(userRepository).save(argThat(u -> u.getKycStatus() == KycStatus.REJECTED));
        verify(kycRequestRepository).save(argThat(kycReq ->
                kycReq.getStatus() == KycStatus.REJECTED &&
                "Documents unclear".equals(kycReq.getReviewNotes())
        ));
    }

    @Test
    void validateKyc_invalidAction_throws() {
        baseUser.setKycStatus(KycStatus.PENDING);
        KycValidationRequest validationReq = new KycValidationRequest();
        validationReq.setAction("INVALID");

        when(userRepository.findById(1L)).thenReturn(Optional.of(baseUser));

        assertThatThrownBy(() -> profileService.validateKyc(1L, validationReq))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Invalid action");
    }

    @Test
    void validateKyc_notPending_throws() {
        baseUser.setKycStatus(KycStatus.APPROVED);
        KycValidationRequest validationReq = new KycValidationRequest();
        validationReq.setAction("APPROVE");

        when(userRepository.findById(1L)).thenReturn(Optional.of(baseUser));

        assertThatThrownBy(() -> profileService.validateKyc(1L, validationReq))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("No pending KYC");
    }

    @Test
    void approveKyc_success() {
        baseUser.setKycStatus(KycStatus.PENDING);
        when(userRepository.findById(1L)).thenReturn(Optional.of(baseUser));
        when(userRepository.save(any())).thenReturn(baseUser);

        profileService.approveKyc(1L);

        verify(userRepository).save(argThat(u ->
                u.getRole() == Role.JASTIPER && u.getKycStatus() == KycStatus.APPROVED));
    }

    @Test
    void rejectKyc_success() {
        baseUser.setKycStatus(KycStatus.PENDING);
        when(userRepository.findById(1L)).thenReturn(Optional.of(baseUser));
        when(userRepository.save(any())).thenReturn(baseUser);

        profileService.rejectKyc(1L);

        verify(userRepository).save(argThat(u -> u.getKycStatus() == KycStatus.REJECTED));
    }

    @Test
    void setUserActive_ban_success() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(baseUser));
        when(userRepository.save(any())).thenReturn(baseUser);

        profileService.setUserActive(1L, false);

        verify(userRepository).save(argThat(u -> !u.isActive()));
    }

    @Test
    void updateProfile_withoutUsername_autogeneratesUsername() {
        // Arrange: User with empty/null username
        User userWithoutUsername = User.builder()
                .id(2L).email("budi@gmail.com").username(null)
                .password("hashed").role(Role.TITIPERS)
                .kycStatus(KycStatus.NONE).active(true).build();

        ProfileUpdateRequest req = new ProfileUpdateRequest();
        req.setDisplayName("Budi Rahman");
        // username NOT provided

        when(userRepository.findById(2L)).thenReturn(Optional.of(userWithoutUsername));
        when(usernameGenerationService.generateUniqueUsername("budi@gmail.com")).thenReturn("budi");
        when(userRepository.save(any())).thenReturn(userWithoutUsername);

        // Act
        profileService.updateProfile(2L, req);

        // Assert
        verify(usernameGenerationService).generateUniqueUsername("budi@gmail.com");
        verify(userRepository).save(argThat(u -> "budi".equals(u.getDisplayUsername())));
    }

    @Test
    void updateProfile_withBlankUsername_autogeneratesUsername() {
        // Arrange: User with blank username
        User userWithoutUsername = User.builder()
                .id(3L).email("john@example.com").username("")
                .password("hashed").role(Role.TITIPERS)
                .kycStatus(KycStatus.NONE).active(true).build();

        ProfileUpdateRequest req = new ProfileUpdateRequest();
        req.setUsername("   "); // blank/whitespace
        req.setDisplayName("John Doe");

        when(userRepository.findById(3L)).thenReturn(Optional.of(userWithoutUsername));
        when(usernameGenerationService.generateUniqueUsername("john@example.com")).thenReturn("john");
        when(userRepository.save(any())).thenReturn(userWithoutUsername);

        // Act
        profileService.updateProfile(3L, req);

        // Assert
        verify(usernameGenerationService).generateUniqueUsername("john@example.com");
        verify(userRepository).save(argThat(u -> "john".equals(u.getDisplayUsername())));
    }
}

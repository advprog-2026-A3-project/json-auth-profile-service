package id.ac.ui.cs.advprog.authnprofile.service;

import id.ac.ui.cs.advprog.authnprofile.dto.*;
import id.ac.ui.cs.advprog.authnprofile.model.*;
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
        KycRequest kycReq = new KycRequest();
        kycReq.setFullName("User Lengkap");

        when(userRepository.findById(1L)).thenReturn(Optional.of(baseUser));
        when(userRepository.save(any())).thenReturn(baseUser);

        profileService.submitKyc(1L, kycReq);

        verify(userRepository).save(argThat(u -> u.getKycStatus() == KycStatus.PENDING));
    }

    @Test
    void submitKyc_alreadyPending_throws() {
        baseUser.setKycStatus(KycStatus.PENDING);
        when(userRepository.findById(1L)).thenReturn(Optional.of(baseUser));

        KycRequest req = new KycRequest();
        req.setFullName("Name");

        assertThatThrownBy(() -> profileService.submitKyc(1L, req))
                .isInstanceOf(IllegalStateException.class);
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
}
package id.ac.ui.cs.advprog.authnprofile.service;

import id.ac.ui.cs.advprog.authnprofile.dto.UserProfileResponse;
import id.ac.ui.cs.advprog.authnprofile.model.Role;
import id.ac.ui.cs.advprog.authnprofile.model.User;
import id.ac.ui.cs.advprog.authnprofile.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;

/**
 * Test suite untuk UserRegistrationService
 * Mengikuti TDD: Write test first, then implementation
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("UserRegistrationService Tests")
class UserRegistrationServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private UsernameGenerationService usernameGenerationService;

    @Mock
    private PasswordValidationService passwordValidationService;

    @Mock
    private PasswordEncoder passwordEncoder;

    private UserRegistrationService userRegistrationService;

    @BeforeEach
    void setUp() {
        userRegistrationService = new UserRegistrationServiceImpl(
                userRepository,
                usernameGenerationService,
                passwordValidationService,
                passwordEncoder
        );
    }

    // ===== Successful Registration Tests =====

    @Test
    @DisplayName("Should register user with valid email and password")
    void registerUser_validEmailPassword_registrationSuccess() {
        // Arrange
        String email = "test@example.com";
        String password = "password123";
        String generatedUsername = "test";

        doNothing().when(passwordValidationService).validateEmail(email);
        doNothing().when(passwordValidationService).validatePassword(password);
        when(userRepository.existsByEmail(email)).thenReturn(false);
        when(usernameGenerationService.generateUniqueUsername(email)).thenReturn(generatedUsername);
        when(passwordEncoder.encode(password)).thenReturn("hashedPassword");

        User savedUser = User.builder()
                .id(1L)
                .email(email)
                .username(generatedUsername)
                .password("hashedPassword")
                .role(Role.TITIPERS)
                .build();
        when(userRepository.save(any(User.class))).thenReturn(savedUser);

        // Act
        UserProfileResponse result = userRegistrationService.registerUser(email, password);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getEmail()).isEqualTo(email);
        assertThat(result.getUsername()).isEqualTo(generatedUsername);
        assertThat(result.getRole()).isEqualTo(Role.TITIPERS);

        verify(passwordValidationService).validateEmail(email);
        verify(passwordValidationService).validatePassword(password);
        verify(userRepository).save(any(User.class));
    }

    @Test
    @DisplayName("Should encrypt password before saving")
    void registerUser_validData_passwordIsEncrypted() {
        // Arrange
        String email = "test@example.com";
        String password = "password123";
        String hashedPassword = "hashedPassword";

        doNothing().when(passwordValidationService).validateEmail(email);
        doNothing().when(passwordValidationService).validatePassword(password);
        when(userRepository.existsByEmail(email)).thenReturn(false);
        when(usernameGenerationService.generateUniqueUsername(email)).thenReturn("test");
        when(passwordEncoder.encode(password)).thenReturn(hashedPassword);

        User savedUser = User.builder()
                .id(1L)
                .email(email)
                .username("test")
                .password(hashedPassword)
                .role(Role.TITIPERS)
                .build();
        when(userRepository.save(any(User.class))).thenReturn(savedUser);

        // Act
        userRegistrationService.registerUser(email, password);

        // Assert
        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(userCaptor.capture());
        User savedUserArg = userCaptor.getValue();

        assertThat(savedUserArg.getPassword()).isEqualTo(hashedPassword);
        assertThat(savedUserArg.getPassword()).isNotEqualTo(password);
    }

    @Test
    @DisplayName("Should set role to TITIPERS by default")
    void registerUser_validData_defaultRoleTitipers() {
        // Arrange
        String email = "test@example.com";
        String password = "password123";

        doNothing().when(passwordValidationService).validateEmail(email);
        doNothing().when(passwordValidationService).validatePassword(password);
        when(userRepository.existsByEmail(email)).thenReturn(false);
        when(usernameGenerationService.generateUniqueUsername(email)).thenReturn("test");
        when(passwordEncoder.encode(password)).thenReturn("hashed");

        User savedUser = User.builder()
                .id(1L)
                .email(email)
                .username("test")
                .password("hashed")
                .role(Role.TITIPERS)
                .build();
        when(userRepository.save(any(User.class))).thenReturn(savedUser);

        // Act
        userRegistrationService.registerUser(email, password);

        // Assert
        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(userCaptor.capture());

        assertThat(userCaptor.getValue().getRole()).isEqualTo(Role.TITIPERS);
    }

    // ===== Validation Tests =====

    @Test
    @DisplayName("Should throw exception when email is invalid")
    void registerUser_invalidEmail_throwsException() {
        String email = "invalid-email";
        String password = "password123";

        doThrow(new IllegalArgumentException("Email format is invalid"))
                .when(passwordValidationService).validateEmail(email);

        assertThatThrownBy(() -> userRegistrationService.registerUser(email, password))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Email format is invalid");
    }

    @Test
    @DisplayName("Should throw exception when password is too short")
    void registerUser_shortPassword_throwsException() {
        String email = "test@example.com";
        String password = "short";

        doNothing().when(passwordValidationService).validateEmail(email);
        doThrow(new IllegalArgumentException("Password must be at least 8 characters long"))
                .when(passwordValidationService).validatePassword(password);

        assertThatThrownBy(() -> userRegistrationService.registerUser(email, password))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("at least 8 characters");
    }

    @Test
    @DisplayName("Should throw exception when email already registered")
    void registerUser_emailAlreadyRegistered_throwsException() {
        String email = "test@example.com";
        String password = "password123";

        doNothing().when(passwordValidationService).validateEmail(email);
        doNothing().when(passwordValidationService).validatePassword(password);
        when(userRepository.existsByEmail(email)).thenReturn(true);

        assertThatThrownBy(() -> userRegistrationService.registerUser(email, password))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Email already registered");

        verify(userRepository, never()).save(any());
    }

    // ===== Check Email Registered Tests =====

    @Test
    @DisplayName("Should return true when email is registered")
    void isEmailRegistered_emailRegistered_returnTrue() {
        when(userRepository.existsByEmail("test@example.com")).thenReturn(true);

        boolean result = userRegistrationService.isEmailRegistered("test@example.com");

        assertThat(result).isTrue();
    }

    @Test
    @DisplayName("Should return false when email is not registered")
    void isEmailRegistered_emailNotRegistered_returnFalse() {
        when(userRepository.existsByEmail("test@example.com")).thenReturn(false);

        boolean result = userRegistrationService.isEmailRegistered("test@example.com");

        assertThat(result).isFalse();
    }
}








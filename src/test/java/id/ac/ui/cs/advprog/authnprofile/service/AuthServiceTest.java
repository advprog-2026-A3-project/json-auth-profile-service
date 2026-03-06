package id.ac.ui.cs.advprog.authnprofile.service;

import id.ac.ui.cs.advprog.authnprofile.dto.*;
import id.ac.ui.cs.advprog.authnprofile.model.Role;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

/**
 * Integration tests untuk AuthServiceImpl facade
 * Test bahwa facade correctly delegates ke underlying services
 */
@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
@DisplayName("AuthService (Facade) Tests")
class AuthServiceTest {

    @Mock
    private UserRegistrationService userRegistrationService;

    @Mock
    private UserAuthenticationService userAuthenticationService;

    private AuthService authService;

    @BeforeEach
    void setUp() {
        authService = new AuthServiceImpl(userRegistrationService, userAuthenticationService);
    }

    // ===== Register Tests =====

    @Test
    @DisplayName("Should delegate register request to UserRegistrationService")
    void register_validRequest_delegateToRegistrationService() {
        // Arrange
        RegisterRequest request = new RegisterRequest();
        request.setEmail("test@example.com");
        request.setPassword("password123");

        UserProfileResponse mockResponse = new UserProfileResponse();
        mockResponse.setEmail("test@example.com");
        mockResponse.setRole(Role.TITIPERS);

        when(userRegistrationService.registerUser("test@example.com", "password123"))
                .thenReturn(mockResponse);

        // Act
        UserProfileResponse result = authService.register(request);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getEmail()).isEqualTo("test@example.com");
    }

    @Test
    @DisplayName("Should propagate registration errors")
    void register_invalidEmail_propagateException() {
        // Arrange
        RegisterRequest request = new RegisterRequest();
        request.setEmail("invalid-email");
        request.setPassword("password123");

        when(userRegistrationService.registerUser("invalid-email", "password123"))
                .thenThrow(new IllegalArgumentException("Email format is invalid"));

        // Act & Assert
        assertThatThrownBy(() -> authService.register(request))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Email format is invalid");
    }

    // ===== Login Tests =====

    @Test
    @DisplayName("Should delegate login request to UserAuthenticationService")
    void login_validRequest_delegateToAuthenticationService() {
        // Arrange
        LoginRequest request = new LoginRequest();
        request.setEmail("test@example.com");
        request.setPassword("password123");

        LoginResponse mockResponse = new LoginResponse("jwt.token.here", "TITIPERS", "testuser");
        when(userAuthenticationService.authenticateUser("test@example.com", "password123"))
                .thenReturn(mockResponse);

        // Act
        LoginResponse result = authService.login(request);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getToken()).isEqualTo("jwt.token.here");
        assertThat(result.getRole()).isEqualTo("TITIPERS");
    }

    @Test
    @DisplayName("Should propagate authentication errors")
    void login_invalidCredentials_propagateException() {
        // Arrange
        LoginRequest request = new LoginRequest();
        request.setEmail("test@example.com");
        request.setPassword("wrongpassword");

        when(userAuthenticationService.authenticateUser("test@example.com", "wrongpassword"))
                .thenThrow(new IllegalArgumentException("User not found"));

        // Act & Assert
        assertThatThrownBy(() -> authService.login(request))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("User not found");
    }
}
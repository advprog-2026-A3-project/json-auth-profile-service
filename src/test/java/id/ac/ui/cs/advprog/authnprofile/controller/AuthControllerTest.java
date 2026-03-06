package id.ac.ui.cs.advprog.authnprofile.controller;

import id.ac.ui.cs.advprog.authnprofile.dto.*;
import id.ac.ui.cs.advprog.authnprofile.model.Role;
import id.ac.ui.cs.advprog.authnprofile.service.AuthService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

/**
 * Unit tests untuk AuthController
 * Menggunakan unit test approach (tidak integration test)
 * untuk memastikan reliability di CI/CD
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("AuthController Unit Tests")
class AuthControllerTest {

    @Mock
    private AuthService authService;

    private AuthController authController;

    @BeforeEach
    void setUp() {
        authController = new AuthController(authService);
    }

    @Test
    @DisplayName("Should register user successfully")
    void register_validRequest_success() {
        // Arrange
        RegisterRequest request = new RegisterRequest();
        request.setEmail("test@example.com");
        request.setPassword("password123");

        UserProfileResponse mockResponse = new UserProfileResponse();
        mockResponse.setId(1L);
        mockResponse.setEmail("test@example.com");
        mockResponse.setUsername("test");
        mockResponse.setRole(Role.TITIPERS);

        when(authService.register(any(RegisterRequest.class)))
                .thenReturn(mockResponse);

        // Act
        var result = authController.register(request);

        // Assert
        assertThat(result.getStatusCode().is2xxSuccessful()).isTrue();
        assertThat(result.getBody()).isNotNull();
        assertThat(result.getBody().getEmail()).isEqualTo("test@example.com");
    }

    @Test
    @DisplayName("Should login user successfully")
    void login_validCredentials_success() {
        // Arrange
        LoginRequest request = new LoginRequest();
        request.setEmail("test@example.com");
        request.setPassword("password123");

        LoginResponse mockResponse = new LoginResponse("jwt.token", "TITIPERS", "test");
        when(authService.login(any(LoginRequest.class)))
                .thenReturn(mockResponse);

        // Act
        var result = authController.login(request);

        // Assert
        assertThat(result.getStatusCode().is2xxSuccessful()).isTrue();
        assertThat(result.getBody()).isNotNull();
        assertThat(result.getBody().getToken()).isEqualTo("jwt.token");
    }
}
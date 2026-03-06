package id.ac.ui.cs.advprog.authnprofile.service;

import id.ac.ui.cs.advprog.authnprofile.dto.LoginResponse;
import id.ac.ui.cs.advprog.authnprofile.model.Role;
import id.ac.ui.cs.advprog.authnprofile.model.User;
import id.ac.ui.cs.advprog.authnprofile.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;

import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Test suite untuk UserAuthenticationService
 * Simple unit test tanpa lenient mocking
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("UserAuthenticationService Tests")
class UserAuthenticationServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private JwtService jwtService;

    @Mock
    private AuthenticationManager authenticationManager;

    private UserAuthenticationService userAuthenticationService;

    @BeforeEach
    void setUp() {
        userAuthenticationService = new UserAuthenticationServiceImpl(
                userRepository,
                jwtService,
                authenticationManager
        );
    }

    @Test
    @DisplayName("Should authenticate user with valid credentials")
    void authenticateUser_validCredentials_success() {
        // Arrange
        String email = "test@example.com";
        String password = "password123";

        User user = User.builder()
                .id(1L)
                .email(email)
                .username("testuser")
                .role(Role.TITIPERS)
                .build();

        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));
        when(jwtService.generateToken(user)).thenReturn("jwt.token.here");

        // Act
        LoginResponse result = userAuthenticationService.authenticateUser(email, password);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getToken()).isEqualTo("jwt.token.here");
        assertThat(result.getRole()).isEqualTo("TITIPERS");
        assertThat(result.getUsername()).isEqualTo("testuser");
    }

    @Test
    @DisplayName("Should throw exception when user not found")
    void authenticateUser_userNotFound_throwsException() {
        // Arrange
        String email = "notfound@example.com";
        String password = "password123";

        when(userRepository.findByEmail(email)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> userAuthenticationService.authenticateUser(email, password))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("User not found");
    }

    @Test
    @DisplayName("Should return token with correct role")
    void authenticateUser_validCredentials_returnCorrectRole() {
        // Arrange
        String email = "jastiper@example.com";
        String password = "password123";

        User user = User.builder()
                .id(2L)
                .email(email)
                .username("jastiper")
                .role(Role.JASTIPER)
                .build();

        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));
        when(jwtService.generateToken(user)).thenReturn("jwt.token");

        // Act
        LoginResponse result = userAuthenticationService.authenticateUser(email, password);

        // Assert
        assertThat(result.getRole()).isEqualTo("JASTIPER");
    }
}



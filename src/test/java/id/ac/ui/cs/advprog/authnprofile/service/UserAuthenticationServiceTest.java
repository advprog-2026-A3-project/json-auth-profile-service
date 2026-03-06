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
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;

import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Test suite untuk UserAuthenticationService
 * Mengikuti TDD: Write test first, then implementation
 */
@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
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

    // ===== Successful Authentication Tests =====

    @Test
    @DisplayName("Should authenticate user with valid credentials")
    void authenticateUser_validCredentials_authenticationSuccess() {
        // Arrange
        String email = "test@example.com";
        String password = "password123";
        String token = "jwt.token.here";

        User user = User.builder()
                .id(1L)
                .email(email)
                .username("testuser")
                .password("hashedPassword")
                .role(Role.TITIPERS)
                .build();

        lenient().doNothing().when(authenticationManager)
                .authenticate(any(UsernamePasswordAuthenticationToken.class));
        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));
        when(jwtService.generateToken(user)).thenReturn(token);

        // Act
        LoginResponse result = userAuthenticationService.authenticateUser(email, password);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getToken()).isEqualTo(token);
        assertThat(result.getRole()).isEqualTo("TITIPERS");
        assertThat(result.getUsername()).isEqualTo("testuser");
    }

    @Test
    @DisplayName("Should return token with user role")
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

        lenient().doNothing().when(authenticationManager)
                .authenticate(any(UsernamePasswordAuthenticationToken.class));
        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));
        when(jwtService.generateToken(user)).thenReturn("token");

        // Act
        LoginResponse result = userAuthenticationService.authenticateUser(email, password);

        // Assert
        assertThat(result.getRole()).isEqualTo("JASTIPER");
    }

    @Test
    @DisplayName("Should call AuthenticationManager with correct credentials")
    void authenticateUser_validCredentials_authenticationManagerCalled() {
        // Arrange
        String email = "test@example.com";
        String password = "password123";

        User user = User.builder()
                .id(1L)
                .email(email)
                .username("testuser")
                .role(Role.TITIPERS)
                .build();

        lenient().doNothing().when(authenticationManager)
                .authenticate(any(UsernamePasswordAuthenticationToken.class));
        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));
        when(jwtService.generateToken(user)).thenReturn("token");

        // Act
        userAuthenticationService.authenticateUser(email, password);

        // Assert
        verify(authenticationManager).authenticate(
                argThat(auth -> auth.getPrincipal().equals(email) &&
                               auth.getCredentials().equals(password))
        );
    }

    @Test
    @DisplayName("Should generate token for authenticated user")
    void authenticateUser_validCredentials_tokenGenerated() {
        // Arrange
        String email = "test@example.com";
        String password = "password123";

        User user = User.builder()
                .id(1L)
                .email(email)
                .username("testuser")
                .role(Role.TITIPERS)
                .build();

        lenient().doNothing().when(authenticationManager)
                .authenticate(any(UsernamePasswordAuthenticationToken.class));
        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));
        when(jwtService.generateToken(user)).thenReturn("generated.token");

        // Act
        userAuthenticationService.authenticateUser(email, password);

        // Assert
        verify(jwtService).generateToken(user);
    }

    // ===== Failed Authentication Tests =====

    @Test
    @DisplayName("Should throw exception when user not found")
    void authenticateUser_userNotFound_throwsException() {
        // Arrange
        String email = "notfound@example.com";
        String password = "password123";

        lenient().doNothing().when(authenticationManager)
                .authenticate(any(UsernamePasswordAuthenticationToken.class));
        when(userRepository.findByEmail(email)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> userAuthenticationService.authenticateUser(email, password))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("User not found");
    }

    @Test
    @DisplayName("Should throw exception when authentication fails")
    void authenticateUser_authenticationFails_throwsException() {
        // Arrange
        String email = "test@example.com";
        String password = "wrongpassword";

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenThrow(new org.springframework.security.core.AuthenticationException("Bad credentials") {});

        // Act & Assert
        assertThatThrownBy(() -> userAuthenticationService.authenticateUser(email, password))
                .isInstanceOf(org.springframework.security.core.AuthenticationException.class);
    }
}


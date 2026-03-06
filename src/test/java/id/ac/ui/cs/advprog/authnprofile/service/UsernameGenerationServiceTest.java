package id.ac.ui.cs.advprog.authnprofile.service;

import id.ac.ui.cs.advprog.authnprofile.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.when;

/**
 * Test suite untuk UsernameGenerationService
 * Simplified to avoid Mockito stubbing issues in CI
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("UsernameGenerationService Tests")
class UsernameGenerationServiceTest {

    @Mock
    private UserRepository userRepository;

    private UsernameGenerationService usernameGenerationService;

    @BeforeEach
    void setUp() {
        usernameGenerationService = new UsernameGenerationServiceImpl(userRepository);
    }

    @Test
    @DisplayName("Should generate username from email when base is unique")
    void generateUniqueUsername_baseUnique_returnBase() {
        String email = "budi@gmail.com";
        when(userRepository.existsByUsername("budi")).thenReturn(false);

        String result = usernameGenerationService.generateUniqueUsername(email);

        assertThat(result).isEqualTo("budi");
    }

    @Test
    @DisplayName("Should add numeric suffix when base username is taken")
    void generateUniqueUsername_baseTaken_addSuffix() {
        String email = "budi@gmail.com";
        when(userRepository.existsByUsername("budi")).thenReturn(true);
        when(userRepository.existsByUsername("budi1")).thenReturn(false);

        String result = usernameGenerationService.generateUniqueUsername(email);

        assertThat(result).isEqualTo("budi1");
    }

    @Test
    @DisplayName("Should return true when username is taken")
    void isUsernameTaken_usernameTaken_returnTrue() {
        when(userRepository.existsByUsername("budi")).thenReturn(true);

        boolean result = usernameGenerationService.isUsernameTaken("budi");

        assertThat(result).isTrue();
    }

    @Test
    @DisplayName("Should return false when username is not taken")
    void isUsernameTaken_usernameNotTaken_returnFalse() {
        when(userRepository.existsByUsername("budi")).thenReturn(false);

        boolean result = usernameGenerationService.isUsernameTaken("budi");

        assertThat(result).isFalse();
    }
}




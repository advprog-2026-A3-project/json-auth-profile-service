package id.ac.ui.cs.advprog.authnprofile.service;

import id.ac.ui.cs.advprog.authnprofile.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

/**
 * Test suite untuk UsernameGenerationService
 * Mengikuti TDD: Write test first, then implementation
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

    // ===== Generate Unique Username Tests =====

    @Test
    @DisplayName("Should generate username from email when base is unique")
    void generateUniqueUsername_baseUnique_returnBase() {
        String email = "budi@gmail.com";
        when(userRepository.existsByUsername("budi")).thenReturn(false);

        String result = usernameGenerationService.generateUniqueUsername(email);

        assertThat(result).isEqualTo("budi");
    }

    @Test
    @DisplayName("Should remove special characters from email base")
    void generateUniqueUsername_emailWithDots_removeSpecialChars() {
        String email = "budi.irawan@gmail.com";
        when(userRepository.existsByUsername("budirawan")).thenReturn(false);

        String result = usernameGenerationService.generateUniqueUsername(email);

        assertThat(result).isEqualTo("budirawan");
    }

    @Test
    @DisplayName("Should handle email with multiple special characters")
    void generateUniqueUsername_emailWithMultipleSpecialChars_removeAll() {
        String email = "budi-irawan_test+tag@gmail.com";
        when(userRepository.existsByUsername("budirawantest")).thenReturn(false);

        String result = usernameGenerationService.generateUniqueUsername(email);

        assertThat(result).isEqualTo("budirawantest");
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
    @DisplayName("Should find next available suffix when multiple taken")
    void generateUniqueUsername_multipleSuffixTaken_findNextAvailable() {
        String email = "budi@gmail.com";
        when(userRepository.existsByUsername("budi")).thenReturn(true);
        when(userRepository.existsByUsername("budi1")).thenReturn(true);
        when(userRepository.existsByUsername("budi2")).thenReturn(true);
        when(userRepository.existsByUsername("budi3")).thenReturn(false);

        String result = usernameGenerationService.generateUniqueUsername(email);

        assertThat(result).isEqualTo("budi3");
    }

    @Test
    @DisplayName("Should handle email with only special characters in local part")
    void generateUniqueUsername_emailOnlySpecialChars_emptyBase() {
        String email = ".-_+@gmail.com";
        // Base will be empty string after removing special chars
        when(userRepository.existsByUsername(anyString())).thenReturn(false);

        String result = usernameGenerationService.generateUniqueUsername(email);

        assertThat(result).isNotNull().isNotEmpty();
    }

    // ===== Check Username Taken Tests =====

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

    @Test
    @DisplayName("Should handle case-sensitive username check")
    void isUsernameTaken_caseSensitive_checkExactMatch() {
        when(userRepository.existsByUsername("Budi")).thenReturn(true);
        when(userRepository.existsByUsername("budi")).thenReturn(false);

        assertThat(usernameGenerationService.isUsernameTaken("Budi")).isTrue();
        assertThat(usernameGenerationService.isUsernameTaken("budi")).isFalse();
    }
}


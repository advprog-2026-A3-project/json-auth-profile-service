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
 * Test suite untuk PasswordValidationService
 * Mengikuti TDD: Write test first, then implementation
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("PasswordValidationService Tests")
class PasswordValidationServiceTest {

    private PasswordValidationService passwordValidationService;

    @BeforeEach
    void setUp() {
        passwordValidationService = new PasswordValidationServiceImpl();
    }

    // ===== Password Validation Tests =====

    @Test
    @DisplayName("Should throw exception when password is null")
    void validatePassword_nullPassword_throwsException() {
        assertThatThrownBy(() -> passwordValidationService.validatePassword(null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("cannot be empty");
    }

    @Test
    @DisplayName("Should throw exception when password is empty")
    void validatePassword_emptyPassword_throwsException() {
        assertThatThrownBy(() -> passwordValidationService.validatePassword(""))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("cannot be empty");
    }

    @Test
    @DisplayName("Should throw exception when password is blank")
    void validatePassword_blankPassword_throwsException() {
        assertThatThrownBy(() -> passwordValidationService.validatePassword("   "))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("cannot be empty");
    }

    @Test
    @DisplayName("Should throw exception when password is less than 8 characters")
    void validatePassword_shortPassword_throwsException() {
        assertThatThrownBy(() -> passwordValidationService.validatePassword("short"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("at least 8 characters");
    }

    @Test
    @DisplayName("Should accept valid password with 8 characters")
    void validatePassword_validPassword8Chars_success() {
        assertThatNoException().isThrownBy(() ->
                passwordValidationService.validatePassword("password")
        );
    }

    @Test
    @DisplayName("Should accept valid password with more than 8 characters")
    void validatePassword_validPasswordLong_success() {
        assertThatNoException().isThrownBy(() ->
                passwordValidationService.validatePassword("password123456")
        );
    }

    // ===== Email Validation Tests =====

    @Test
    @DisplayName("Should throw exception when email is null")
    void validateEmail_nullEmail_throwsException() {
        assertThatThrownBy(() -> passwordValidationService.validateEmail(null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("cannot be empty");
    }

    @Test
    @DisplayName("Should throw exception when email is empty")
    void validateEmail_emptyEmail_throwsException() {
        assertThatThrownBy(() -> passwordValidationService.validateEmail(""))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("cannot be empty");
    }

    @Test
    @DisplayName("Should throw exception when email is blank")
    void validateEmail_blankEmail_throwsException() {
        assertThatThrownBy(() -> passwordValidationService.validateEmail("   "))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("cannot be empty");
    }

    @Test
    @DisplayName("Should throw exception when email format is invalid (no @)")
    void validateEmail_invalidFormatNoAt_throwsException() {
        assertThatThrownBy(() -> passwordValidationService.validateEmail("invalidemail.com"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("invalid");
    }

    @Test
    @DisplayName("Should throw exception when email format is invalid (no domain)")
    void validateEmail_invalidFormatNoDomain_throwsException() {
        assertThatThrownBy(() -> passwordValidationService.validateEmail("user@"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("invalid");
    }

    @Test
    @DisplayName("Should accept valid email")
    void validateEmail_validEmail_success() {
        assertThatNoException().isThrownBy(() ->
                passwordValidationService.validateEmail("user@example.com")
        );
    }

    @Test
    @DisplayName("Should accept valid email with plus sign")
    void validateEmail_validEmailWithPlus_success() {
        assertThatNoException().isThrownBy(() ->
                passwordValidationService.validateEmail("user+tag@example.com")
        );
    }

    @Test
    @DisplayName("Should accept valid email with underscore and dot")
    void validateEmail_validEmailWithSpecialChars_success() {
        assertThatNoException().isThrownBy(() ->
                passwordValidationService.validateEmail("user_name.test@example.co.uk")
        );
    }
}


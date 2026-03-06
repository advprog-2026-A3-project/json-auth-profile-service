package id.ac.ui.cs.advprog.authnprofile.service;

/**
 * Service untuk validate password sesuai requirement.
 * Memenuhi Single Responsibility Principle.
 */
public interface PasswordValidationService {
    /**
     * Validate password sesuai requirement
     * @param password password yang divalidasi
     * @throws IllegalArgumentException jika password tidak memenuhi requirement
     */
    void validatePassword(String password);

    /**
     * Validate email format
     * @param email email yang divalidasi
     * @throws IllegalArgumentException jika email format tidak valid
     */
    void validateEmail(String email);
}


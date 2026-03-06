package id.ac.ui.cs.advprog.authnprofile.service;

/**
 * Service untuk register user dengan validasi lengkap.
 * Memenuhi Single Responsibility Principle.
 */
public interface UserRegistrationService {
    /**
     * Register user baru dengan email dan password
     * @param email email pengguna
     * @param password password pengguna
     * @return UserProfileResponse
     */
    id.ac.ui.cs.advprog.authnprofile.dto.UserProfileResponse registerUser(String email, String password);

    /**
     * Check apakah email sudah terdaftar
     * @param email email yang dicek
     * @return true jika sudah terdaftar
     */
    boolean isEmailRegistered(String email);
}


package id.ac.ui.cs.advprog.authnprofile.service;

/**
 * Service untuk generate dan manage username.
 * Memenuhi Single Responsibility Principle.
 */
public interface UsernameGenerationService {
    /**
     * Generate username unik dari email
     * @param email email pengguna
     * @return username yang unik
     */
    String generateUniqueUsername(String email);

    /**
     * Check apakah username sudah ada
     * @param username username yang dicek
     * @return true jika sudah ada
     */
    boolean isUsernameTaken(String username);
}


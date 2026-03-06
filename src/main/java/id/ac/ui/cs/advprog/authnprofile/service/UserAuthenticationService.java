package id.ac.ui.cs.advprog.authnprofile.service;

/**
 * Service untuk handle login dan token management.
 * Memenuhi Single Responsibility Principle.
 */
public interface UserAuthenticationService {
    /**
     * Authenticate user dengan email dan password
     * @param email email pengguna
     * @param password password pengguna
     * @return LoginResponse dengan JWT token
     */
    id.ac.ui.cs.advprog.authnprofile.dto.LoginResponse authenticateUser(String email, String password);
}


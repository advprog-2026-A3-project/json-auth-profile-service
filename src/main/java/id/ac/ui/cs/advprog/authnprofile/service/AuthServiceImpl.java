package id.ac.ui.cs.advprog.authnprofile.service;

import id.ac.ui.cs.advprog.authnprofile.dto.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * Facade service untuk authentication dan profile.
 * Menggunakan composition dari specialized services (Single Responsibility).
 * Memenuhi Dependency Inversion Principle dengan menggunakan interface.
 */
@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRegistrationService userRegistrationService;
    private final UserAuthenticationService userAuthenticationService;

    @Override
    public UserProfileResponse register(RegisterRequest request) {
        return userRegistrationService.registerUser(request.getEmail(), request.getPassword());
    }

    @Override
    public LoginResponse login(LoginRequest request) {
        return userAuthenticationService.authenticateUser(request.getEmail(), request.getPassword());
    }
}
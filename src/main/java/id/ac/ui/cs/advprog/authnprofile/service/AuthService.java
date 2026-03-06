package id.ac.ui.cs.advprog.authnprofile.service;

import id.ac.ui.cs.advprog.authnprofile.dto.*;

public interface AuthService {
    UserProfileResponse register(RegisterRequest request);
    LoginResponse login(LoginRequest request);
}
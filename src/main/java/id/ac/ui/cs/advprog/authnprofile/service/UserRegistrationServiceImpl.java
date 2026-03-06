package id.ac.ui.cs.advprog.authnprofile.service;

import id.ac.ui.cs.advprog.authnprofile.dto.UserProfileResponse;
import id.ac.ui.cs.advprog.authnprofile.model.Role;
import id.ac.ui.cs.advprog.authnprofile.model.User;
import id.ac.ui.cs.advprog.authnprofile.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserRegistrationServiceImpl implements UserRegistrationService {

    private final UserRepository userRepository;
    private final UsernameGenerationService usernameGenerationService;
    private final PasswordValidationService passwordValidationService;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public UserProfileResponse registerUser(String email, String password) {
        // Validasi input
        passwordValidationService.validateEmail(email);
        passwordValidationService.validatePassword(password);

        // Check apakah email sudah terdaftar
        if (isEmailRegistered(email)) {
            throw new IllegalArgumentException("Email already registered");
        }

        // Generate unique username
        String generatedUsername = usernameGenerationService.generateUniqueUsername(email);

        // Create user dengan role default TITIPERS
        User newUser = User.builder()
                .email(email)
                .username(generatedUsername)
                .password(passwordEncoder.encode(password))
                .role(Role.TITIPERS)
                .build();

        User savedUser = userRepository.save(newUser);
        return UserProfileResponse.from(savedUser);
    }

    @Override
    public boolean isEmailRegistered(String email) {
        return userRepository.existsByEmail(email);
    }
}


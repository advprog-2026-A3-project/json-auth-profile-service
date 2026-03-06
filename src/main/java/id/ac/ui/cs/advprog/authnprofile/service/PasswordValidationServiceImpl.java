package id.ac.ui.cs.advprog.authnprofile.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.regex.Pattern;

@Service
@RequiredArgsConstructor
public class PasswordValidationServiceImpl implements PasswordValidationService {

    private static final int MIN_PASSWORD_LENGTH = 8;
    private static final String EMAIL_PATTERN = "^[A-Za-z0-9+_.-]+@(.+)$";
    private static final Pattern EMAIL_REGEX = Pattern.compile(EMAIL_PATTERN);

    @Override
    public void validatePassword(String password) {
        if (password == null || password.isBlank()) {
            throw new IllegalArgumentException("Password cannot be empty");
        }

        if (password.length() < MIN_PASSWORD_LENGTH) {
            throw new IllegalArgumentException(
                    String.format("Password must be at least %d characters long", MIN_PASSWORD_LENGTH)
            );
        }
    }

    @Override
    public void validateEmail(String email) {
        if (email == null || email.isBlank()) {
            throw new IllegalArgumentException("Email cannot be empty");
        }

        if (!EMAIL_REGEX.matcher(email).matches()) {
            throw new IllegalArgumentException("Email format is invalid");
        }
    }
}


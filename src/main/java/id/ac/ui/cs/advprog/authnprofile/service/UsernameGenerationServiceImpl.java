package id.ac.ui.cs.advprog.authnprofile.service;

import id.ac.ui.cs.advprog.authnprofile.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UsernameGenerationServiceImpl implements UsernameGenerationService {

    private final UserRepository userRepository;

    @Override
    public String generateUniqueUsername(String email) {
        String baseUsername = extractBaseFromEmail(email);

        if (!userRepository.existsByUsername(baseUsername)) {
            return baseUsername;
        }

        return generateUsernameWithSuffix(baseUsername);
    }

    @Override
    public boolean isUsernameTaken(String username) {
        return userRepository.existsByUsername(username);
    }

    /**
     * Extract base username dari email dengan menghapus karakter non-alphanumeric
     * Contoh: budi@gmail.com -> budi, budi.irawan@gmail.com -> budirawan
     */
    private String extractBaseFromEmail(String email) {
        return email.split("@")[0].replaceAll("[^a-zA-Z0-9_]", "");
    }

    /**
     * Generate username dengan suffix numerik
     * Contoh: budi -> budi1, budi1 -> budi2, dst
     */
    private String generateUsernameWithSuffix(String baseUsername) {
        int suffix = 1;
        while (userRepository.existsByUsername(baseUsername + suffix)) {
            suffix++;
        }
        return baseUsername + suffix;
    }
}


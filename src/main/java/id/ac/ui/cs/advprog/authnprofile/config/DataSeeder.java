package id.ac.ui.cs.advprog.authnprofile.config;

import id.ac.ui.cs.advprog.authnprofile.model.Role;
import id.ac.ui.cs.advprog.authnprofile.model.User;
import id.ac.ui.cs.advprog.authnprofile.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class DataSeeder implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public DataSeeder(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) throws Exception {
        if (userRepository.findByEmail("admin@gmail.com").isEmpty()) {
            User superAdmin = User.builder()
                    .email("admin@gmail.com")
                    .username("adminjson")
                    .password(passwordEncoder.encode("@4Dm1NjS0N"))
                    .displayName("Admin")
                    .role(Role.ADMIN)
                    .active(true)
                    .build();

            userRepository.save(superAdmin);
            System.out.println("Akun Admin berhasil dibuat!");
        }
    }
}
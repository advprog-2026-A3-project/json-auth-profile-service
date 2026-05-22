package id.ac.ui.cs.advprog.authnprofile.service;

import id.ac.ui.cs.advprog.authnprofile.dto.LoginResponse;
import id.ac.ui.cs.advprog.authnprofile.model.User;
import id.ac.ui.cs.advprog.authnprofile.repository.UserRepository;
import io.micrometer.core.instrument.MeterRegistry;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserAuthenticationServiceImpl implements UserAuthenticationService {

    private final UserRepository userRepository;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final MeterRegistry meterRegistry;

    @Override
    public LoginResponse authenticateUser(String email, String password) {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(email, password)
            );
            User user = userRepository.findByEmail(email)
                    .orElseThrow(() -> new IllegalArgumentException("User not found"));

            String token = jwtService.generateToken(user);
            meterRegistry.counter("auth.login.status", "result", "success").increment();
            return new LoginResponse(token, user.getRole().name(), user.getDisplayUsername());
        } catch (Exception e) {
            meterRegistry.counter("auth.login.status", "result", "failure").increment();
            throw e;
        }
    }
}


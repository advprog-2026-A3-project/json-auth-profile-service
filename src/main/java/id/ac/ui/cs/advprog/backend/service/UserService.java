package id.ac.ui.cs.advprog.backend.service;

import id.ac.ui.cs.advprog.backend.model.User;
import id.ac.ui.cs.advprog.backend.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User registerUser(String username, String password, String fullName) {
        if (userRepository.findByUsername(username).isPresent()) {
            throw new IllegalArgumentException("Username sudah terpakai!");
        }
        User newUser = new User(username, password, fullName);
        return userRepository.save(newUser);
    }

    public User loginUser(String username, String password) {
        Optional<User> userOpt = userRepository.findByUsername(username);
        if (userOpt.isPresent() && userOpt.get().getPassword().equals(password)) {
            return userOpt.get();
        }
        throw new IllegalArgumentException("Username atau password salah!");
    }

    public User getProfile(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("User tidak ditemukan!"));
    }

    public User updateProfile(String username, String fullName, String bio) {
        User user = getProfile(username);
        user.setFullName(fullName);
        user.setBio(bio);
        return userRepository.save(user);
    }
}
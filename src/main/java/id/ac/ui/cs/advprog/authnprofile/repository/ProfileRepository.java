package id.ac.ui.cs.advprog.authnprofile.repository;

import id.ac.ui.cs.advprog.authnprofile.model.Profile;
import id.ac.ui.cs.advprog.authnprofile.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ProfileRepository extends JpaRepository<Profile, Long> {
    Optional<Profile> findByUser(User user);
    Optional<Profile> findByUsername(String username);
    boolean existsByUsername(String username);
}

